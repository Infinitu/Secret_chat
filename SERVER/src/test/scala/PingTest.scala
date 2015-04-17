import akka.actor.{ActorSystem, Props}
import akka.io.Tcp.{Close, Received, Write}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import the.accidental.billionaire.secretchat.actor.TcpHandler
import the.accidental.billionaire.secretchat.protocol._
import the.accidental.billionaire.secretchat.security.UserData

import scala.concurrent.duration._

/**
 * Created by infinitu on 2015. 4. 10..
 */
class PingTest(_system:ActorSystem) extends TestKit(_system) with WordSpecLike with MockFactory with BeforeAndAfterAll with ImplicitSender{
  def this()= this(ActorSystem("PingTest"))

  implicit val notLogined:Option[UserData] = None

  "PingResponder" should {
    "respond ping" in {
      val actor = TestActorRef(Props(new TcpHandler(self)))
      actor ! Received(Ping.serialize)
      expectMsg(Write(Pong.serialize))
    }

    "close connection when client does not reply in timeout duration" in {
      val actor = TestActorRef(Props(new TcpHandler(self){
        override val timeout: FiniteDuration = 1 second
      }))
      actor ! PingResponder.sendPing
      expectMsg(Write(Ping.serialize))
      expectMsg(2.seconds, Close)
    }

    "not close connection when client reply pong in timeout duration" in {
      val actor = TestActorRef(Props(new TcpHandler(self){
        override val timeout: FiniteDuration = 1 second
      }))
      actor ! PingResponder.sendPing
      expectMsg(Write(Ping.serialize))
      actor ! Received(Pong.serialize)
      expectNoMsg(2.seconds)
    }
  }



}
