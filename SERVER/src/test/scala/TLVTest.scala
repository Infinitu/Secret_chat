import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp.Received
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.ByteString
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, _}
import the.accidental.billionaire.secretchat.actor.protocol.{Command, CommandCase, TLVInterpreter}
import the.accidental.billionaire.secretchat.actor.security.UserData
import the.accidental.billionaire.secretchat.utils.ReceivePipeline


/**
 * Created by infinitu on 15. 4. 3..
 */
class TLVTest(_system:ActorSystem) extends TestKit(_system) with FlatSpecLike with Matchers with MockFactory with BeforeAndAfterAll with ImplicitSender{

  def this()= this(ActorSystem("MySpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  def getTestActor(func:Any=>Unit):TestActorRef[_]={
    TestActorRef(Props(new ReceivePipeline with TLVInterpreter {
      override def receive: Actor.Receive = {case z=>func(z)}
      override val connection: ActorRef = TLVTest.this.self
    }))
  }

  "TLV Interpreter" should "parse a command" in {
    val mockFunc = mockFunction[Any,Unit]
    val ref = getTestActor(mockFunc)
    val ac = ref.underlyingActor.asInstanceOf[ReceivePipeline]

    mockFunc expects Command(0x1111,0x0002,ByteString("ab")) once()
    ac.aroundReceive(ac.receive,Received(ByteString(0x11,0x11,0x00,0x00,0x00,0x02)++ByteString("ab")))
  }

  "TLV Interpreter" should "parse a command in fragments" in {
    val mockFunc = mockFunction[Any,Unit]
    val ref = getTestActor(mockFunc)
    val ac = ref.underlyingActor.asInstanceOf[ReceivePipeline]

    mockFunc expects Command(0x1111,0x0002,ByteString("ab")) once()
    ac.aroundReceive(ac.receive,Received(ByteString(0x11)))
    ac.aroundReceive(ac.receive,Received(ByteString(0x11,0x00,0x00,0x00)))
    ac.aroundReceive(ac.receive,Received(ByteString(0x02)++ByteString("ab")))
  }

  "TLV Interpreter" should "parse two command in fragments" in {
    val mockFunc = mockFunction[Any,Unit]
    val ref = getTestActor(mockFunc)
    val ac = ref.underlyingActor.asInstanceOf[ReceivePipeline]

    mockFunc expects Command(0x1111,0x0002,ByteString("ab")) once()
    mockFunc expects Command(0x2222,0x0004,ByteString("cdef")) once()
    ac.aroundReceive(ac.receive,Received(ByteString(0x11,0x11,0x00,0x00,0x00)))
    ac.aroundReceive(ac.receive,Received(ByteString(0x02)++ByteString("ab")++ByteString(0x22,0x22,0x00)))
    ac.aroundReceive(ac.receive,Received(ByteString(0x00,0x00,0x04)++ByteString("cdef")))
  }

  "CommandCase" should "serialize header, length and body" in {
    val testCase = new CommandCase{
      override val header: Int = 0x1234
      override def body(implicit userData: Option[UserData]): ByteString = ByteString("test")
    }
    implicit val test:Option[UserData] = None
    testCase.serialize should be (ByteString(0x12,0x34,0,0,0,4) ++ ByteString("test"))
  }
}
