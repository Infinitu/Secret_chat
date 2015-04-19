package the.accidental.billionaire.secretchat.actor

import akka.actor.{Actor, ActorPath}
import com.redis.RedisClientPool
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MessageDispatcher{
  val actorPath = "MessageDispatcher"

  val config:Config = ConfigFactory.load().getConfig("redis")

  val redis_host = config.getString("host")
  val redis_port = config.getInt("port")
  val presence_collection_name = config.getString("presence_name")

  val redisPool = new RedisClientPool(redis_host,redis_port)

  case class RegisterClientConnection(address:String)
  case class UnregisterClientConnection(address:String)
  case class SendMessage(address:String,sender:String,timestamp:Long,msg:Any)
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
