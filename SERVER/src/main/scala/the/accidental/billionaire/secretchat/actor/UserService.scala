package the.accidental.billionaire.secretchat.actor

import akka.actor.Actor
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.{ConfigFactory, Config}
import the.accidental.billionaire.secretchat.actor.security.UserData

/**
 * Created by infinitu on 15. 3. 24..
 */

object UserService{
  val config:Config = ConfigFactory.load().getConfig("mongo")

  val mongo_host = config.getString("host")
  val mongo_port = config.getInt("port")
  val mongo_dbname = config.getString("dbname")

  val collectionName = "user"
  val col_deviceId = "deviceId"
  val col_accessToken = "accessToken"
  val col_encryptToken = "encryptToken"

  val actorPath = "UserService"

  case class LoginReqest(deviceId:String, accessToken:String)
  case class LoginOkay(userdata:UserData)
  case object LoginFailed
}
class UserService extends Actor{
  import UserService._
  val DB = MongoClient(mongo_host,mongo_port)(mongo_dbname)
  val coll = DB(collectionName)
  override def receive: Receive = {
    case LoginReqest(devid,token)=>
      val msg =
        coll.findOne(MongoDBObject(col_deviceId->devid,col_accessToken->token))
          .map(user=>UserData(devid,token,user.get(col_encryptToken).asInstanceOf[String]))
          .map(LoginOkay)
      sender ! msg.getOrElse(LoginFailed)
    case _=>
  }
}
