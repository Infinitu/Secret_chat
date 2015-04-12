package the.accidental.billionaire.secretchat.actor

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.Tcp.Close
import the.accidental.billionaire.secretchat.protocol._
import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 15. 3. 28..
 */
object TcpHandler{

}
class TcpHandler(override val connection:ActorRef) extends Actor
  with ReceivePipeline with TLVInterpreter with BodyInterpreter with PingResponder{

  override def receive = unauthorized
  val log = Logging(this)

  def unauthorized:Receive = {
    case req @ SessionLoginRequest(version,id,token,os,device)=>
      log.info("loginRequested : {}", req)
      //todo check version,os,device
      val userService = context.system.actorSelection("/user/"+UserService.actorPath)
      userService ! UserService.LoginReqest(id,token)
    case UserService.LoginOkay(userdata)=>
      log.info("session authorized successfully : {}",userdata)
      this.userData = Some(userdata)
      context become authorized
      writeToConnection( SessionLoginOkay() )
    case UserService.LoginFailed=>
      writeToConnection( AuthFailed("LoginFailed") )
      closeSession()
    case _=>
  }
  def authorized():Receive = {
    case _=>
  }

  override def pingTimeout() = {
    //todo Other...
    closeSession()
  }

  def closeSession(): Unit ={
    connection ! Close
    context stop self
  }
}
