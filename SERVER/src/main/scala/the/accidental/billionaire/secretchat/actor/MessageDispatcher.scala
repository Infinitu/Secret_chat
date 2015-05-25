package the.accidental.billionaire.secretchat.actor

import akka.actor.{Actor, ActorPath}
import play.api.libs.json.{JsValue, Json}
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker.{FriendRequest, FriendsEstablished, FriendsEstablishedFromRandomRoom}
import the.accidental.billionaire.secretchat.protocol.{BodyWritable, JsonWrites, StringBodyWritable}
import the.accidental.billionaire.secretchat.security.UserData
import the.accidental.billionaire.secretchat.utils.RedisStore._

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
    private def apply[T<:BodyWritable](json:JsValue)(writes: JsonWrites[SendMessage[T]]):SendMessage[T] ={
      writes.fromJson(json)
    }
    private def apply(typeStr:String, json:JsValue):SendMessage[_] = typeStr match{
      case `stringTypeStr` =>
        apply[StringBodyWritable](json)(stringSendMessageWrites)
      case `friendRequestTypeStr`=>
        apply[FriendRequest](json)(friendRequestSendMessageWrites)
      case `friendsEstablishedTypeStr`=>
        apply[FriendsEstablished](json)(friendsEstablishedSendMessageWrites)
      case `friendsEstablishedFromRandomRoomTypeStr`=>
        apply[FriendsEstablishedFromRandomRoom](json)(friendsEstablishedInRandomSendMessageWrites)
    }
    def fromJson(json:JsValue):SendMessage[_] =
      SendMessage.apply((json \ "msgType").as[String],json)
  }

  class SendMessageWirtes[T<:BodyWritable](val typeStr:String)(implicit writes:JsonWrites[T]) extends JsonWrites[SendMessage[T]]{

    override def toJson(obj: SendMessage[T]): JsValue = {
      Json.obj(
        "msgType"->typeStr,
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
  val friendRequestTypeStr = classOf[FriendRequest].toString
  val friendsEstablishedTypeStr = classOf[FriendsEstablished].toString
  val friendsEstablishedFromRandomRoomTypeStr = classOf[FriendsEstablishedFromRandomRoom].toString
  implicit val stringSendMessageWrites =
    new SendMessageWirtes[StringBodyWritable](stringTypeStr)
  implicit val friendRequestSendMessageWrites =
    new SendMessageWirtes[FriendRequest](friendRequestTypeStr)
  implicit val friendsEstablishedSendMessageWrites =
    new SendMessageWirtes[FriendsEstablished](friendsEstablishedTypeStr)
  implicit val friendsEstablishedInRandomSendMessageWrites =
    new SendMessageWirtes[FriendsEstablishedFromRandomRoom](friendsEstablishedFromRandomRoomTypeStr)
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
