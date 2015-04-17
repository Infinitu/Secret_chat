package the.accidental.billionaire.secretchat

import akka.actor.{ActorRef, ActorPath, Actor}
import akka.actor.Actor.Receive
import com.redis.RedisClientPool
import com.typesafe.config.{ConfigFactory, Config}
import the.accidental.billionaire.secretchat.MessageDispatcher.{UnregisterClientConnection, RegisterClientConnection}
import the.accidental.billionaire.secretchat.protocol.{ReceiveMessageArrival, SendChatMessage}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MessageDispatcher{
  val config:Config = ConfigFactory.load().getConfig("redis")

  val redis_host = config.getString("host")
  val redis_port = config.getInt("port")
  val presence_collection_name = config.getString("presence_name")

  val redisPool = new RedisClientPool(redis_host,redis_port)

  case class RegisterClientConnection(address:String)
  case class UnregisterClientConnection(address:String)
  case class SendMessage(address:String,msg:Any)
}

class MessageDispatcher(missingPath:String) extends Actor{

  def this()=this(MissingMessageDispatcher.pathname)

  val missingDispatcher = context.actorSelection(missingPath)

  import MessageDispatcher._
  val localConnectionMap = mutable.HashMap[String,ActorPath]()

  override def receive: Receive = {
    case RegisterClientConnection(address)=>
      localConnectionMap += address->sender().path
      registerOnRedis(address,sender().path)

    case UnregisterClientConnection(address)=>
      localConnectionMap -= address
      unregisterOnRedis(address)
    case m @ SendMessage(address,_)=>
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
