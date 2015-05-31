import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import com.github.simplyscala.MongoEmbedDatabase
import com.typesafe.config.{ConfigFactory, Config}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}
import redis.embedded.RedisServer
import the.accidental.billionaire.secretchat.actor.MessageDispatcher.SendMessage
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker
import the.accidental.billionaire.secretchat.actor.{RandomMessageExchanger, MessageDispatcher}

/**
 * Created by infinitu on 2015. 5. 28..
 */
class RandomQueueTest(_system:ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers with MockFactory with BeforeAndAfterAll with BeforeAndAfter with ImplicitSender with MongoEmbedDatabase{
//  def this()= this(ActorSystem("RandomExchangeTest"))
//
//  val dispatcher = TestActorRef(Props(new MessageDispatcher(self.path.toString)))
//  val matchmaker = TestActorRef(Props(new Matchmaker(dispatcher.path.toString, exchanger.path.toString)))
//  val user1Actor = TestActorRef(Props(new MessageTagger("user1",self)))
//  val user2Actor = TestActorRef(Props(new MessageTagger("user2",self)))
//  val user1 = "address1"
//  val user2 = "address2"
//  val randomroom = "random_room1"
//
//  val config:Config = ConfigFactory.load().getConfig("redis")
//  val port = config.getInt("port")
//  val redis = new RedisServer(port)
//
//  import MessageTagger.Redirect
//  override protected def beforeAll(): Unit ={
//    redis.start()
//    user1Actor ! Redirect(dispatcher,MessageDispatcher.RegisterClientConnection(user1))
//    user2Actor ! Redirect(dispatcher,MessageDispatcher.RegisterClientConnection(user2))
//    exchanger.underlyingActor.asInstanceOf[RandomMessageExchanger].setMembers(randomroom,user1,user2)
//  }
//
//
//
//  override protected def afterAll(): Unit ={
//    redis.stop()
//    TestKit.shutdownActorSystem(system)
//  }
//
//  import MessageTagger.MsgTag
//
//  "Matchmaker" should {
//    "establish friends request and response" in {
//      val req = Matchmaker.FriendRequest(user1,user2,"hello world")
//      matchmaker ! req
//
//      val dispatcherPath = dispatcher.path.toString
//
//      assert(
//        expectMsgPF(){
//          case MsgTag(`dispatcherPath`,"user2",
//          SendMessage(`user2`,"system_matchmaker",_,`req`))=>
//            true
//          case _=>
//            false
//        }
//      )
//
//      matchmaker ! Matchmaker.FriendRequestAnswer(user2,user1,"accept")
//
//      assert(
//        expectMsgPF(){
//          case MsgTag(`dispatcherPath`,"user2",
//          SendMessage(`user2`,"system_matchma
}
