package the.accidental.billionaire.secretchat.actor

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.redis.RedisClientPool
import com.typesafe.config.{ConfigFactory, Config}
import the.accidental.billionaire.secretchat.actor.RandomMessageExchanger.SendRandomMessage
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker.{FriendRequest, FriendsEstablishedFromRandomRoom}
import the.accidental.billionaire.secretchat.protocol.BodyWritable
import the.accidental.billionaire.secretchat.utils.RedisStore._

/**
 * Created by infinitu on 2015. 5. 16..
 */
object RandomMessageExchanger{
  val actorPath = "RandomMessageExchanger"
  case class SendRandomMessage(address:String,sender:String,timestamp:Long,msg:BodyWritable)
}

class RandomMessageExchanger(dispatcherPath:String) extends Actor{
  def this()=this("/user/"+MessageDispatcher.actorPath)
  val msgDispatcher = context.system.actorSelection(dispatcherPath)

  override def receive: Receive = {
    case SendRandomMessage(address,sender,timestamp,msg)=>
      getMembers(address).flatMap{
        case (`sender`,partner)=>Some(partner)
        case (partner,`sender`)=>Some(partner)
        case pair@(_,_) if sender.startsWith("system_")=>Some(pair)
        case _=>None
      }.foreach{
        case partner:String =>
          msgDispatcher ! MessageDispatcher.SendMessage(partner,address,timestamp,msg)
          //Seding from User and Exchange an Address And Radndomroom Number.
        case pair@(_,_)=>
          pair
            .productIterator
            .foreach{x=>
              msgDispatcher ! MessageDispatcher.SendMessage(x.asInstanceOf[String],sender,timestamp,msg)
            }
          //Sending from System User to Pushe Message in Randomroom
      }
    case FriendRequest(senderAddr,room,msg)=>
      getMembers(room).flatMap{
        case (`senderAddr`,partner)=>Some(partner)
        case (partner,`senderAddr`)=>Some(partner)
        case _=>None
      }.foreach {partner=>
        sender() ! FriendRequest(room,partner,msg)
      }
    case FriendsEstablishedFromRandomRoom(random,_,enckey)=>
      getMembers(random).foreach{case (foo,bar)=>
        msgDispatcher ! MessageDispatcher.SendMessage(foo,Matchmaker.systemUserName,System.currentTimeMillis(),
          FriendsEstablishedFromRandomRoom(random,bar,enckey))
        msgDispatcher ! MessageDispatcher.SendMessage(bar,Matchmaker.systemUserName,System.currentTimeMillis(),
          FriendsEstablishedFromRandomRoom(random,foo,enckey))
      }
    case _=>
  }

  def getMembers(roomNum:String):Option[(String, String)] ={
    redisPool.withClient{client=>
      client.hget(randomRoom_collection_name,roomNum)
    }.flatMap{x=>
      val members = x.split('\\')
      if(members.length<2)
        None
      else
        Some(members(0)->members(1))
    }
  }

  def setMembers(roomNum:String, member1:String, member2:String):Boolean ={
    redisPool.withClient(_.hset(randomRoom_collection_name,roomNum,member1+'\\'+member2))
  }
}
