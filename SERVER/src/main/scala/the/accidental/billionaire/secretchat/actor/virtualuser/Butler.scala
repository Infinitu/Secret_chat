package the.accidental.billionaire.secretchat.actor.virtualuser

import akka.actor.Actor
import akka.actor.Actor.Receive
import play.api.libs.json.Json
import the.accidental.billionaire.secretchat.actor.MessageDispatcher
import the.accidental.billionaire.secretchat.actor.MessageDispatcher.SendMessage
import the.accidental.billionaire.secretchat.actor.virtualuser.Butler.BreakUp
import the.accidental.billionaire.secretchat.protocol.BodyWritable
import the.accidental.billionaire.secretchat.security.UserData
import the.accidental.billionaire.secretchat.utils.RedisStore._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by infinitu on 2015. 5. 16..
 */
object Butler{
  val actorPath = "butler"
  val systemUserName = "system_butler"
  case class BreakUp(senderAddr:String, roomNumber:String)
  case class RoomBroken(roomNumber:String, code:Int, cause:String) extends BodyWritable{
    override def writeToString(implicit userData: Option[UserData]): String =
      Json.obj(
        "address"->roomNumber,
        "code"->code,
        "cause"->cause
      ).toString()
  }
}
class Butler(dispatcherPath:String) extends Actor{
  def this() = this("/user/"+MessageDispatcher.actorPath)
  import Butler._
  val msgDispatcher = context.system.actorSelection(dispatcherPath)

  override def receive: Receive = {
    case BreakUp(senderAddr,roomNumber)=>
      getMembers(roomNumber).foreach {
        _.flatMap {
          case (`senderAddr`, partner) => Some(partner)
          case (partner, `senderAddr`) => Some(partner)
          case _ => None
        }.foreach { partner =>
          msgDispatcher ! SendMessage(partner,systemUserName,System.currentTimeMillis(),
            RoomBroken(roomNumber,1,"partner had left out"))
        }
      }
    case _=>
  }

  def getMembers(roomNum:String):Future[Option[(String, String)]] = Future{
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

  def remRandomRoom(roomNum:String) = Future{
    redisPool.withClient(_.hdel(randomRoom_collection_name,roomNum))
  }
}
