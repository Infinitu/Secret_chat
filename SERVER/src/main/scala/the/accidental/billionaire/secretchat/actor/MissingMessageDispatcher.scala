package the.accidental.billionaire.secretchat.actor

import akka.actor.Actor
import play.api.libs.json.{JsValue, Json}
import the.accidental.billionaire.secretchat.actor.MessageDispatcher._
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker.{FriendRequest, FriendsEstablishedFromRandomRoom, FriendsEstablished}
import the.accidental.billionaire.secretchat.protocol.{FriendsRequest, MessingMessageNotification, StringBodyWritable, JsonWrites}
import the.accidental.billionaire.secretchat.security.UserData

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MissingMessageDispatcher{
  val actorPath = "missing"
  case class GetMissingMessage(userdata:UserData)
  case class CheckMissingMessage(userData: UserData)
}

class MissingMessageDispatcher extends Actor{
  import MissingMessageDispatcher._
  import the.accidental.billionaire.secretchat.utils.RedisStore._
  /*
    val stringSendMessageWrites = new SendMessageWirtes[StringBodyWritable]
  val friendsEstablishedSendMessageWrites = new SendMessageWirtes[FriendsEstablished]
  val friendsEstablishedInRandomSendMessageWrites = new SendMessageWirtes[FriendsEstablishedFromRandomRoom]
}
   */
  override def receive: Receive ={
    case msg@SendMessage(dest,_,_,_:StringBodyWritable)=>
      storeMessage(dest,msg.asInstanceOf[SendMessage[StringBodyWritable]]);

    case msg@SendMessage(dest,_,_,_:Matchmaker.FriendRequest)=>
      storeMessage(dest,msg.asInstanceOf[SendMessage[Matchmaker.FriendRequest]]);

    case msg@SendMessage(dest,_,_,_:FriendsEstablished)=>
      storeMessage(dest,msg.asInstanceOf[SendMessage[Matchmaker.FriendsEstablished]]);

    case msg@SendMessage(dest,_,_,_:FriendsEstablishedFromRandomRoom)=>
      storeMessage(dest,msg.asInstanceOf[SendMessage[Matchmaker.FriendsEstablishedFromRandomRoom]]);

    case GetMissingMessage(ud)=>
      getMessage(ud.userAddress).foreach{msgs=>
        remSet(ud.userAddress)

        msgs.foreach(x=>x.foreach{ msg=>
          sender() ! SendMessage.fromJson(Json.parse(msg).as[JsValue])
        })
      }
    case CheckMissingMessage(ud)=>
      didMissed(ud.userAddress).foreach{cnt=>
        if(cnt>0)
          sender() ! MessingMessageNotification(cnt.toInt);
      }
    case _=>
  }

  def storeMessage[T](destAddress:String,msg:T)(implicit writes:JsonWrites[T]): Unit = Future{
    redisPool.withClient{client=>
      client.sadd(missing_collection_name+destAddress, writes.toJson(msg).toString())
    }
  }

  def getMessage(destAddress:String):Future[Option[Set[String]]] = Future{
    redisPool.withClient { client =>
      client.smembers(missing_collection_name + destAddress)
    }.map(_.filter(_.isDefined).map(_.get))
  }

  def remSet(destAddress:String): Unit = Future{
    redisPool.withClient(_.del(missing_collection_name+destAddress))
  }
  def didMissed(destAddress:String):Future[Long] = Future{
    redisPool.withClient(_.scard(missing_collection_name+destAddress)).getOrElse(0)
  }
}
