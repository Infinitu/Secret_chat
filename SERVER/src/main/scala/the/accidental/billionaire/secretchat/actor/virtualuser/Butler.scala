package the.accidental.billionaire.secretchat.actor.virtualuser

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * Created by infinitu on 2015. 5. 16..
 */
object Butler{
  val actorPath = "aaa"
}
class Butler extends Actor{
  override def receive: Receive = {
    case _=>
  }
}
