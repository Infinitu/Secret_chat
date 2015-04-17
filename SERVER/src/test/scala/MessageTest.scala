import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import com.redis.RedisClient
import com.typesafe.config.{ConfigFactory, Config}
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import redis.embedded.RedisServer
import the.accidental.billionaire.secretchat.MessageDispatcher.SendMessage
import the.accidental.billionaire.secretchat.{MissingMessageDispatcher, MessageDispatcher}
import the.accidental.billionaire.secretchat.security.UserData

/**
 * Created by infinitu on 2015. 4. 17..
 */
class MessageTest(_system:ActorSystem) extends TestKit(_system) with WordSpecLike with BeforeAndAfterAllConfigMap with Matchers with MockFactory with ImplicitSender{
  def this()= this(ActorSystem("MessageTest"))

  implicit val notLogined:Option[UserData] = None

  val config:Config = ConfigFactory.load().getConfig("redis")
  val port = config.getInt("port")
  val redis = new RedisServer(port)
  val presence_collection_name = config.getString("presence_name")
  redis.start()

  override protected def afterAll(configMap: ConfigMap): Unit ={
    TestKit.shutdownActorSystem(system)
    redis.stop()
  }

  val actor = TestActorRef(Props(new MessageDispatcher(self.path.toString)))

  "MessageDispatcher" should {

    val redisClient = new RedisClient("localhost",port)
    "register and unregister path to redis" in{
      val address = "address_test"
      actor ! MessageDispatcher.RegisterClientConnection(address)
      Thread.sleep(500) //because of async prcoessing.
      redisClient.hget(presence_collection_name,address) should be (Some(self.path.toString))

      actor ! MessageDispatcher.UnregisterClientConnection(address)
      Thread.sleep(500) //because of async prcoessing.
      redisClient.hget(presence_collection_name,address) should be (None)
    }

    "send message to Missing if address is not logined" in {
      val address = "missingAddr"
      val myAddr = "myAddr"
      val message = MessageDispatcher.SendMessage(myAddr, SendMessage(address,""))
      actor ! message
      expectMsg(message)
    }

  }
}
