package the.accidental.billionaire.secretchat.actor

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive
import akka.io.Tcp.Close
import akka.util.ByteString
import the.accidental.billionaire.secretchat.actor.protocol.{PingResponder, BodyInterpreter, TLVInterpreter}
import the.accidental.billionaire.secretchat.actor.security.UserData
import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 15. 3. 28..
 */
object TcpHandler{

}
class TcpHandler(override val connection:ActorRef) extends Actor with ReceivePipeline with TLVInterpreter with BodyInterpreter with PingResponder{

  override def receive = unauthorized

  def unauthorized:Receive = {case _=>}
  def authorized:Receive = {case _=>}

  override def pingTimeout() = {
    //todo Other...
    connection ! Close
  }
}
