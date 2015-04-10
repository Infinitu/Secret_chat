import akka.actor.Actor.Receive
import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.io.Tcp.{Close, Write, Received}
import akka.testkit.{TestActorRef, TestActor, ImplicitSender, TestKit}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{WordSpecLike, BeforeAndAfterAll, FlatSpecLike}
import the.accidental.billionaire.secretchat.actor.TcpHandler
import the.accidental.billionaire.secretchat.actor.protocol._
import the.accidental.billionaire.secretchat.actor.security.UserData
import the.accidental.billionaire.secretchat.utils.ReceivePipeline
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
