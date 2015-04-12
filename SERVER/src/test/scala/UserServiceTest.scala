import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.{Config, ConfigFactory}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import the.accidental.billionaire.secretchat.actor.UserService
import the.accidental.billionaire.secretchat.actor.security.UserData


/**
 * Created by infinitu on 2015. 4. 12..
 */
class UserServiceTest(_system:ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers with MockFactory with BeforeAndAfterAll with ImplicitSender with MongoEmbedDatabase{
  def this()= this(ActorSystem("PingTest"))

  var mongoProps:MongodProps = null

  override protected def beforeAll(): Unit ={
    val config:Config = ConfigFactory.load().getConfig("mongo")
    val port = config.getInt("port")
    mongoProps = mongoStart(port = port)
  }

  override protected def afterAll(): Unit ={
    mongoStop(mongoProps)
    TestKit.shutdownActorSystem(system)
  }

  "UserService" should {
    import UserService._
    val config:Config = ConfigFactory.load().getConfig("mongo")
    val client = MongoClient("localhost",config.getInt("port"))
    val DB = client getDB config.getString("dbname")
    val actorRef = TestActorRef(Props(new UserService))

    "login success with correct parameter condition" in {
      if(DB.collectionExists(collectionName))
        (DB getCollection collectionName).drop()
      val coll = DB getCollection collectionName
      val devid = "test_deviceID"
      val accToken = "test_accToken"
      val encToken = "test_encToken"
      coll insert MongoDBObject(col_deviceId->devid, col_accessToken->accToken, col_encryptToken->encToken)

      actorRef ! LoginReqest(devid,accToken)
      expectMsg(LoginOkay(UserData(devid,accToken,encToken)))
    }

    "login failed with incorrect parameter condition" in {
      val coll = DB getCollection collectionName
      actorRef ! LoginReqest("someid","someToken")
      expectMsg(LoginFailed)
    }
  }
}
