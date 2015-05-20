package the.accidental.billionaire.secretchat.actor

import java.awt.event.InputEvent

import akka.actor.{Actor, ActorPath}
import com.redis.RedisClientPool
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.json.{JsValue, JsObject, Json}
import the.accidental.billionaire.secretchat.actor.MessageDispatcher.SendMessage
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker.{FriendsEstablishedFromRandomRoom, FriendsEstablished}
import the.accidental.billionaire.secretchat.protocol.{StringBodyWritable, JsonWrites, BodyWritable}
import the.accidental.billionaire.secretchat.security.UserData

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import the.accidental.billionaire.secretchat.utils.RedisStore._

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MessageDispatcher{
  val actorPath = "MessageDispatcher"

  case class RegisterClientConnection(address:String)
  case class UnregisterClientConnection(address:String)
  case class SendMessage[T<:BodyWritable](address:String,sender:String,timestamp:Long,msg:T) extends BodyWritable{
    override def writeToString(implicit userData: Option[UserData]) =
      Json.obj(
        "address" -> address,
        "sender" -> sender,
        "timestamp" -> timestamp,
        "msg" -> msg.writeToString(userData)
      ).toString()
  }
  object SendMessage{
    def apply[T<:BodyWritable](json:JsValue)(writes: JsonWrites[SendMessage[T]]):SendMessage[T] ={
      writes.fromJson(json)
    }
    def apply(typeStr:String, json:JsValue):SendMessage[_] = typeStr match{
      case `stringTypeStr` =>
        apply[StringBodyWritable](json)(stringSendMessageWrites)
      case `friendsEstablishedTypeStr`=>
        apply[FriendsEstablished](json)(friendsEstablishedSendMessageWrites)
      case `friendsEstablishedFromRandomRoomTypeStr`=>
        apply[FriendsEstablishedFromRandomRoom](json)(friendsEstablishedInRandomSendMessageWrites)
    }
  }

  class SendMessageWirtes[T<:BodyWritable](implicit writes:JsonWrites[T]) extends JsonWrites[SendMessage[T]]{
    override def toJson(obj: SendMessage[T]): JsValue = {
      Json.obj(
        "address" -> obj.address,
        "sender" -> obj.sender,
        "timestamp" -> obj.timestamp,
        "msg"->writes.toJson(obj.msg)
      )
    }

    override def fromJson(json: JsValue): SendMessage[T] = {
      SendMessage(
        (json \ "address").as[String],
        (json \ "sender").as[String],
        (json \ "timestamp").as[Long],
        writes.fromJson((json \ "msg").as[JsValue]))
    }
  }
  import the.accidental.billionaire.secretchat.protocol.stringJsonWrites
  val stringTypeStr = classOf[String].toString
  val friendsEstablishedTypeStr = classOf[FriendsEstablished].toString
  val friendsEstablishedFromRandomRoomTypeStr = classOf[FriendsEstablishedFromRandomRoom].toString
  val stringSendMessageWrites = new SendMessageWirtes[StringBodyWritable]
  val friendsEstablishedSendMessageWrites = new SendMessageWirtes[FriendsEstablished]
  val friendsEstablishedInRandomSendMessageWrites = new SendMessageWirtes[FriendsEstablishedFromRandomRoom]
}

class MessageDispatcher(missingPath:String) extends Actor{

  def this()=this("user/"+MissingMessageDispatcher.actorPath)

  val missingDispatcher = context.system.actorSelection(missingPath)

  import MessageDispatcher._
  val localConnectionMap = mutable.HashMap[String,ActorPath]()

  override def receive: Receive = {
    case RegisterClientConnection(address)=>
      localConnectionMap += address->sender().path
      registerOnRedis(address,sender().path)

    case UnregisterClientConnection(address)=>
      localConnectionMap -= address
      unregisterOnRedis(address)

    case m @ SendMessage(address,sender,_,_)=>
      withActorPath(address){pathOpt=>
        val dest = pathOpt.map{path=>
          context.actorSelection(path)
        }.getOrElse{
          missingDispatcher
        }
        dest ! m
      }
  }

  def registerOnRedis(address:String,path:ActorPath) = Future{
    redisPool.withClient{client=>
      client.hset(presence_collection_name,address,path.toString)
    }
  }

  def unregisterOnRedis(address:String) = Future{
    redisPool.withClient{client=>
      client.hdel(presence_collection_name,address)
    }
  }

  def getPathStrOnRedis(address:String) = Future{
    redisPool.withClient{client=>
      client.hget(presence_collection_name,address)
    }
  }

  def withActorPath[T](address:String)(func:Option[ActorPath]=>T):Future[T]={
    localConnectionMap.get(address).map{x=>Future(Some(x))}
    .getOrElse{
      getPathStrOnRedis(address)
        .map(_.map(ActorPath.fromString))
    }
    .map(func.apply)
  }
}
