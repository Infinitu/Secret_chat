package the.accidental.billionaire.secretchat

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MissingMessageDispatcher{
  val pathname = "missing"
}
class MissingMessageDispatcher extends Actor{
  override def receive: Receive = ???
}
