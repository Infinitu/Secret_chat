package the.accidental.billionaire.secretchat.actor

import akka.actor.Actor

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MissingMessageDispatcher{
  val actorPath = "missing"
}
class MissingMessageDispatcher extends Actor{
  override def receive: Receive = ???
}
