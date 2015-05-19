package the.accidental.billionaire.secretchat.actor.virtualuser


import akka.actor.Actor
import akka.actor.Actor.Receive
import play.api.libs.json._
import the.accidental.billionaire.secretchat.actor.{RandomMessageExchanger, MessageDispatcher}
import the.accidental.billionaire.secretchat.actor.MessageDispatcher.SendMessage
import the.accidental.billionaire.secretchat.actor.virtualuser.Matchmaker.{FriendsEstablished, FriendsEstablishedFromRandomRoom, FriendRequestAnswer, FriendRequest}
import the.accidental.billionaire.secretchat.protocol.BodyWritable
import the.accidental.billionaire.secretchat.security.{AddressEncryptor, UserData}
import the.accidental.billionaire.secretchat.utils.RedisStore._

/**
 * Created by infinitu on 2015. 5. 16..
 * 친구관계, 랜덤매칭을 관장하는 관계 관리 엑터.
 */
object  Matchmaker{
  val actorPath = "matchmaker"
  val systemUserName = "system_matchmaker"

  case class FriendRequest(senderAddress:String, address:String,message:String) extends BodyWritable{

    override def writeToString(implicit userData: Option[UserData])={
      Json.obj(
        "type"->"friends_requested",
        "message"->Json.obj(
          "address"->AddressEncryptor.addressEncrypt(senderAddress),
          "message"->message
        )
      ).toString()
    }
  }
  case class FriendRequestAnswer(senderAddress:String, address:String, status:String)
  case class FriendsEstablished(address:String, encKey:String) extends BodyWritable{
    override def writeToString(implicit userData: Option[UserData])={
      Json.obj(
        "type"->"established",
        "message"->Json.obj(
          "address"->AddressEncryptor.addressEncrypt(address),
          "encKey"->encKey
        )
      ).toString()
    }
  }
  case class FriendsEstablishedFromRandomRoom(randomroonNumber:String, address:String, encKey:String) extends BodyWritable{
    override def writeToString(implicit userData: Option[UserData])={
      Json.obj(
        "type"->"established_from_randomroom",
        "message"->Json.obj(
          "room_num"->randomroonNumber,
          "address"->AddressEncryptor.addressEncrypt(address),
          "encKey"->encKey
        )
      ).toString()
    }
  }
}

class Matchmaker(dispatcherPath:String, exchangerPath:String) extends Actor{
  def this() = this("/user/"+MessageDispatcher.actorPath, "/user/"+RandomMessageExchanger.actorPath)

  import Matchmaker._
  val msgDispatcher = context.system.actorSelection(dispatcherPath)
  val randomExchanger = context.system.actorSelection(exchangerPath)

  override def receive: Receive = {
    case req@FriendRequest(sender,address,msg) if address.startsWith("random_")=>
      randomExchanger ! req
    case req@FriendRequest(sender,address,msg)=>
      saveRequest(sender,address)
      msgDispatcher ! SendMessage(address, systemUserName, System.currentTimeMillis(), req)
    case FriendRequestAnswer(sender,address,"accept") if address.startsWith("random_")=>
      if(verifyRequest(address, sender)){
        randomExchanger ! FriendsEstablishedFromRandomRoom(address,sender, makeEncKey)
      }
    case FriendRequestAnswer(sender,address,"accept") =>
      if(verifyRequest(address, sender)){
        val enckey = makeEncKey
        msgDispatcher ! SendMessage(sender,systemUserName,System.currentTimeMillis(),FriendsEstablished(address,enckey))
        msgDispatcher ! SendMessage(address,systemUserName,System.currentTimeMillis(),FriendsEstablished(sender,enckey))
        deleteRequest(address,sender)
      }
    case FriendRequestAnswer(sender,address,"deny")=>
      deleteRequest(address,sender)
      //ignore
    case _=>
  }

  def makeEncKey:String={
    "" //todo
  }


  def verifyRequest(fromAddress:String, anserAddress:String):Boolean={
    redisPool.withClient{client =>
      client.sismember(friendsRequest_collection_name+":"+fromAddress, anserAddress)
    }
  }

  def deleteRequest(fromAddress:String, anserAddress:String): Unit ={
    redisPool.withClient{client =>
      client.srem(friendsRequest_collection_name+":"+fromAddress, anserAddress)
    }
  }

  def saveRequest(from:String, to:String):Option[Long] ={
    redisPool.withClient{client =>
      client.sadd(friendsRequest_collection_name+":"+from, to)
    }
  }
}
