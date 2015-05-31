package the.accidental.billionaire.secretchat.actor

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.Tcp.{PeerClosed, Close}
import the.accidental.billionaire.secretchat.actor.MissingMessageDispatcher.GetMissingMessage
import the.accidental.billionaire.secretchat.actor.virtualuser.{Butler, Matchmaker}
import the.accidental.billionaire.secretchat.protocol._
import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 15. 3. 28..
 */
object TcpHandler{
  val warning_pendingQueue = 10 // todo configable
  val accepted_pendingQueue = 50 // todo configable
}
class TcpHandler(override val connection:ActorRef) extends Actor
  with ReceivePipeline with TLVInterpreter with BodyInterpreter with PingResponder{
  import MessageDispatcher._


  override def receive = unauthorized
  val log = Logging(this)
  val msgDispatcher = context.system.actorSelection("/user/"+MessageDispatcher.actorPath)
  val matchmaker = context.system.actorSelection("/user/"+Matchmaker.actorPath)
  val randomExchanger = context.system.actorSelection("/user/"+RandomMessageExchanger.actorPath)
  val randomQueue = context.system.actorSelection("/user/"+RandomQueue.actorPath)
  val butler = context.system.actorSelection("/user/"+Butler.actorPath)
  val missingMsgDispatcher = context.system.actorSelection("/user/"+MissingMessageDispatcher.actorPath)
  var pendingQueue:List[ReceiveMessageArrivalPlain] = Nil

  def unauthorized:Receive = {
    case req @ SessionLoginRequest(version,id,token,os,appversion,device)=>
      log.info("loginRequested : {}", req)
      //todo check version,os,device
      val userService = context.system.actorSelection("/user/"+UserService.actorPath)
      userService ! UserService.LoginReqest(id,token)
    case UserService.LoginOkay(userdata)=>
      log.info("session authorized successfully : {}",userdata)
      this.userData = Some(userdata)
      msgDispatcher ! RegisterClientConnection(userdata.userAddress)
      missingMsgDispatcher ! MissingMessageDispatcher.CheckMissingMessage(userdata)
      context become authorized
      writeToConnection( SessionLoginOkay() )
    case UserService.LoginFailed=>
      writeToConnection( AuthFailed("LoginFailed"))
      closeSession()

    case x=>
      println(x)
  }

  def authorized():Receive = {
    case msg:SendChatMessage=>
      try{
        val timestamp = System.currentTimeMillis()
        msgDispatcher ! SendMessage(msg.address,userData.get.userAddress,timestamp,msg.messageBody)
        writeToConnection(SendingMessageSuccessful(timestamp))
      }
      catch{case e:Exception=>
          log.error(e,"error in receive Sending Message from connection")
        writeToConnection(SendingMessageFailed(0,"error"))
      }
    case SendSystemChatMessage(address,jsonStr)=>

    case msg:SendRandomChatMessage=>

    case SendMessage(_,senderAddr,timestamp,msg)
      if sender().path.toString contains MissingMessageDispatcher.actorPath=>
      receiveMissingMessage(senderAddr,timestamp,msg.writeToString)
    case SendMessage(_,sender,timestamp,msg)=>
      receiveMessage(sender,timestamp,msg.writeToString)

    case ReceivingMessageSuccessful(senderAddr,time,idx)=>
      receiveMessageSuccessful(senderAddr,time,idx)

    case ReceivingMessageFailed(senderAddr,time,idx)=>
      receiveMessageFailed(senderAddr,time,idx)

    case GetMissingMessageRequest=>
      missingMsgDispatcher ! GetMissingMessage(userData.get)

    case FriendsRequest(address,msg)=>
      matchmaker ! Matchmaker.FriendRequest(userData.get.userAddress, address,msg)
      writeToConnection(FriendsRequestSendSuccessfully)

    case FriendsResponse(address, status)=>
      matchmaker ! Matchmaker.FriendRequestAnswer(userData.get.userAddress, address,status)
      writeToConnection(FriendsRequestSendSuccessfully)

    case msg:MessingMessageNotification=>
      writeToConnection(msg)

    case RandomMatchingEnqueue()=>
      randomQueue ! RandomQueue.RandomEnQueue(userData.get,RandomQueue.MatchCondition(0))

    case RandomMatchingDequeue=>
      randomQueue ! RandomQueue.RandomDeQueue(userData.get)

    case RandomChatExit(address)=>
      butler ! Butler.BreakUp(userData.get.userAddress,address)
      
    case RandomQueue.Successfully=>
      writeToConnection(EnqueueSuccessful)

    case msg:MatchEstablished=>
      writeToConnection(msg)

    case PeerClosed=>
      finalizeSession

    case _=>
  }

  def receiveMessage(sender:String,time:Long,msgStr:String): Unit ={
    val idx = pendingQueue.count(x=>x.senderAddress == sender && x.sendDateTime == time)
    //문제 없겠죠?? 1ms사이에 설마 2건을 요청하고 1ms사이에 한건만 받을수가 없겠죠?
    if(idx>0)
      log.debug("more than 1 message in 1 mils from : {}", sender)
    val msg = ReceiveMessageArrival(sender, time,idx,msgStr)
    receiveMessage(msg)
  }

  def receiveMissingMessage(sender:String,time:Long,msgStr:String): Unit ={
    val idx = pendingQueue.count(x=>x.senderAddress == sender && x.sendDateTime == time)
    //문제 없겠죠?? 1ms사이에 설마 2건을 요청하고 1ms사이에 한건만 받을수가 없겠죠?
    if(idx>0)
      log.debug("more than 1 message in 1 mils from : {}", sender)
    val msg = MissingMessage(ReceiveMessageArrival(sender, time,idx,msgStr))
    receiveMessage(msg)
  }

  def receiveMessage(msg:ReceiveMessageArrivalPlain): Unit ={
    pendingQueue = pendingQueue :+ msg

    //send ping when warning queue size
    if(pendingQueue.size >= TcpHandler.warning_pendingQueue)
      sendPing()

    //close session when over accepted queue size
    if(pendingQueue.size >= TcpHandler.accepted_pendingQueue)
      closeSession()

    writeToConnection(msg)
  }

  def receiveMessageSuccessful(addr:String,time:Long,idx:Int)={
    pendingQueue.find(x=> x.senderAddress == addr && x.sendDateTime == time && x.idx==idx)
      .foreach(removeInPendingQueue)

  }

  def receiveMessageFailed(addr:String,time:Long,idx:Int)={
    pendingQueue.find(x=> x.senderAddress == addr && x.sendDateTime == time && x.idx==idx)
      .foreach { failed =>
        removeInPendingQueue(failed)
        receiveMessage(failed)
      }
  }

  def removeInPendingQueue(msg:ReceiveMessageArrivalPlain): Unit ={
    val index = pendingQueue.indexOf(msg)
    val left = pendingQueue.take(index)
    pendingQueue = pendingQueue.drop(index+1)
    left foreach receiveMessage
  }

  override def pingTimeout() = {
    //todo Other...
    closeSession()
  }

  def closeSession(): Unit ={
    connection ! Close
    finalizeSession
  }

  def finalizeSession: Unit = {
    userData.foreach { data =>
      msgDispatcher ! UnregisterClientConnection(data.userAddress)
    }
    pendingQueue
      .map(recv => SendMessage(this.userData.get.userAddress, recv.senderAddress, recv.sendDateTime, recv.message))
      .foreach(missingMsgDispatcher.!)
    context stop self
  }
}
