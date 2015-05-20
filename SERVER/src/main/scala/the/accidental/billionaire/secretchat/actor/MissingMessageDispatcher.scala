package the.accidental.billionaire.secretchat.actor

import akka.actor.Actor
import the.accidental.billionaire.secretchat.actor.MessageDispatcher.SendMessage
import the.accidental.billionaire.secretchat.security.UserData

/**
 * Created by infinitu on 2015. 4. 17..
 */
object MissingMessageDispatcher{
  val actorPath = "missing"
  case class Reconnected(userdata:UserData)
}

class MissingMessageDispatcher extends Actor{
  override def receive: Receive ={
    case SendMessage(address,sender,timestamp,msg)=>

    case _=>
  }
}
