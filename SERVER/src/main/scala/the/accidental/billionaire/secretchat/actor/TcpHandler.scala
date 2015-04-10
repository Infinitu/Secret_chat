package the.accidental.billionaire.secretchat.actor

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.Close
import the.accidental.billionaire.secretchat.protocol.{BodyInterpreter, PingResponder, TLVInterpreter}
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
