import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import com.github.simplyscala.{MongodProps, MongoEmbedDatabase}
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.{ConfigFactory, Config}
import org.bson.types.ObjectId
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers, WordSpecLike}
import redis.embedded.RedisServer
import the.accidental.billionaire.secretchat.actor.RandomMessageExchanger.SendRandomMessage
import the.accidental.billionaire.secretchat.actor.{RandomMessageExchanger, MessageDispatcher, UserService}
import the.accidental.billionaire.secretchat.actor.UserService._
import the.accidental.billionaire.secretchat.security.UserData
import the.accidental.billionaire.secretchat.protocol.{StringBodyWritable, String2BodyWritable}

/**
 * Created by infinitu on 2015. 5. 20..
 */
class RandomExchangerTest(_system:ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers with MockFactory with BeforeAndAfterAll with BeforeAndAfter with ImplicitSender with MongoEmbedDatabase{
  def this()= this(ActorSystem("RandomExchangeTest"))

  val dispatcher = TestActorRef(Props(new MessageDispatcher(self.path.toString)))
  val exchanger = TestActorRef(Props(new RandomMessageExchanger(dispatcher.path.toString)))
  val user1Actor = TestActorRef(Props(new MessageTagger("user1",self)))
  val user2Actor = TestActorRef(Props(new MessageTagger("user2",self)))
  val user1 = "address1"
  val user2 = "address2"
  val randomroom = "random_room1"

  val config:Config = ConfigFactory.load().getConfig("redis")
  val port = config.getInt("port")
  val redis = new RedisServer(port)

  import MessageTagger.Redirect
  override protected def beforeAll(): Unit ={
    redis.start()
    user1Actor ! Redirect(dispatcher,MessageDispatcher.RegisterClientConnection(user1))
    user2Actor ! Redirect(dispatcher,MessageDispatcher.RegisterClientConnection(user2))
    exchanger.underlyingActor.asInstanceOf[RandomMessageExchanger].setMembers(randomroom,user1,user2)
  }



  override protected def afterAll(): Unit ={
    redis.stop()
    TestKit.shutdownActorSystem(system)
  }

  import MessageTagger.MsgTag

  "RandomExchanger" should {

    "excahnge randomroom number for sendRandomMessage classes" in{
      exchanger ! SendRandomMessage(randomroom, user1, 1, "hello user2")
      expectMsg(
        MsgTag(dispatcher.path.toString,"user2",
          MessageDispatcher.SendMessage(user2,randomroom,1,StringBodyWritable("hello user2"))))

      exchanger ! SendRandomMessage(randomroom, user2, 2, "nice to meet you user1!")
      expectMsg(
        MsgTag(dispatcher.path.toString,"user1",
          MessageDispatcher.SendMessage(user1,randomroom,2,StringBodyWritable("nice to meet you user1!"))))
    }
  }
}
