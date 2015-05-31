package the.accidental.billionaire.secretchat.actor.benderpush

import akka.actor.{Props, Actor}
import the.accidental.billionaire.secretchat.actor.benderpush.BenderPusher.PushMessage


/**
 * Created by infinitu on 2015. 5. 20..
 */
object BenderPusher{
  val actorPath = "benderPush"
  val apnsKeyPattern = "apns::(.+)".r
  case class PushMessage(key:String, msg:String, missingCount:Int)
}
class BenderPusher extends Actor{
  import BenderPusher._
  val apnsPusher = context.actorOf(Props[APNSPusher],"apnspusher")
  override def receive: Receive = {
    case PushMessage(apnsKeyPattern(token), msg, missingCount)=>
      apnsPusher ! APNSPusher.ApnsPush(token,msg,missingCount)
    case _=>
  }
}
