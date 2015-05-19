import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor}

/**
 * Created by infinitu on 2015. 5. 20..
 */
object MessageTagger{
  case class MsgTag(from:String, to:String, content:Any)
  case class Redirect(to:ActorRef , content:Any)
}
class MessageTagger(receiverName:String, redirectTo:ActorRef) extends Actor{
  import MessageTagger._
  override def receive: Receive = {
    case Redirect(to,content) => to ! content
    case x=> redirectTo ! MsgTag(sender().path.toString,receiverName,x)
  }
}
