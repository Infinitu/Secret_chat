package the.accidental.billionaire.secretchat.actor

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive
import akka.event.Logging
import the.accidental.billionaire.secretchat.protocol.MatchEstablished
import the.accidental.billionaire.secretchat.security.UserData
import the.accidental.billionaire.secretchat.utils.RedisStore._
import the.accidental.billionaire.secretchat.utils.crypto.KeyGenerator

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success,Failure}

/**
 * Created by infinitu on 2015. 5. 27..
 */
object RandomQueue {
  val actorPath = "RandomQueue"

  case class RandomEnQueue(userdata:UserData, condition: MatchCondition)
  case class RandomDeQueue(userdata:UserData)
  case object Successfully
  case class MatchCondition(gender:Int){
    def matchUserData(userData: UserData)={
      true
    }
  }

}

class RandomQueue extends Actor{
  import RandomQueue._
  val log = Logging(this)
  val queue = mutable.ArrayBuffer[(ActorRef,RandomEnQueue)]()
  override def receive: Receive = {
    case msg@RandomEnQueue(userdata,matchCondition)=>
      val requester = sender()
      requester ! Successfully
      queue.find{
        case (ref,info)=>
          info.condition.matchUserData(userdata) &&
          matchCondition.matchUserData(info.userdata)
      } match{
        case found@Some((ref,waiter))=>
          queue -= found.get
          val roomnum = generateRoomNumber
          setMembers(roomnum,waiter.userdata.userAddress,userdata.userAddress)
          .onComplete{
            case Success(succeed) if succeed=>
              requester ! MatchEstablished(roomnum)
              ref ! MatchEstablished(roomnum)
            case Success(succeed) if !succeed=>
              log.error("setMember return False.")
            case Failure(t)=>
              log.error(t,"error on setmember to %s with %s and %s",
                roomnum,
                waiter.userdata.userAddress,
                userdata.userAddress)
          }
        case None=>
          queue += requester->msg
      }
    case RandomDeQueue(userdata)=>
      queue.find(_._2.userdata.userAddress equals userdata.userAddress)
        .foreach(queue.-=)
    case _=>
  }

  def generateRoomNumber={
    KeyGenerator.genRandomHex(16)
  }

  def setMembers(roomNum:String, member1:String, member2:String):Future[Boolean]= Future{
    redisPool.withClient(_.hset(randomRoom_collection_name,roomNum,member1+'\\'+member2))
  }

}
