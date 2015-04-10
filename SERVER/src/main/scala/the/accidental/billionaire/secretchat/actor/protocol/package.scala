package the.accidental.billionaire.secretchat.actor

import akka.io.Tcp.Write
import akka.util.ByteString
import the.accidental.billionaire.secretchat.actor.security.UserData

/**
 * Created by infinitu on 15. 4. 3..
 */
package object protocol {
  val DefaultCharset = "UTF-8"

  case class Command(header: Int, length: Int, body: ByteString) {
    val complete =
      if (length == body.length)
        Some(this)
      else if (length < body.length)
        Some(Command(header, length, body.take(length)))
      else
        None

    def ++(additionalBody: ByteString): Command =
      Command(header, length, body ++ additionalBody)

    def overplusBody: Option[ByteString] =
      if (body.length > length)
        Some(body.drop(length))
      else
        None
  }

  implicit def Command2Write(command:CommandCase)(implicit userData: Option[UserData]):Write = Write(command.serialize)


  trait CommandCase{
    val header:Int
    def body(implicit userData: Option[UserData]):ByteString
    def serialize(implicit userData:Option[UserData]) = {
      val body = this.body
      ByteString((header & 0xFF00) >> 8, header & 0x00FF) ++
        ByteString((body.length & 0xFF000000) >> 24, (body.length & 0x00FF0000) >> 16, (body.length & 0x0000FF00) >> 8, body.length & 0x000000FF) ++
        body
    }
  }

  object AddressEncrypter {
    def addressEncrypt(plainAddress:String)(implicit userData:Option[UserData]):String = {
      //todo implement.
      plainAddress
    }
    def addressDecrypt(encrptedAddress:String)(implicit userData:Option[UserData]):String = {
      //todo implement
      encrptedAddress
    }
  }

  /**
   * 0x0001
   */
  case object Ping extends CommandCase {
    override val header:Int = 0x0001
    def body(implicit userData: Option[UserData]) = ByteString()
  }

  /**
   * 0x0002
   */
  case object Pong extends  CommandCase{
    override val header: Int = 0x0002
    def body(implicit userData: Option[UserData]) = ByteString()
  }

  /**
   * 0x1001
   */
  case class SessionLoginRequest(version: String, deviceId: String, accessToken: String, osName: String, deviceName: String) extends  CommandCase{
    override val header: Int = 0x1001
    private def this(bodyParam:Array[String]) = this(bodyParam(0), bodyParam(1), bodyParam(2), bodyParam(3), bodyParam(4))
    def this(body:ByteString)=this(body.decodeString(DefaultCharset).split('|'))
    def body(implicit userData: Option[UserData]) = ByteString("%s|%s|%s|%s|%s|".format(version,deviceId,accessToken,osName,deviceName),DefaultCharset)
  }

  /**
   * 0x1002
   */
  case class SessionLoginOkay() extends CommandCase{
    override val header: Int = 0x1002
    def body(implicit userData: Option[UserData]) = ByteString()
  }
  /**
   * 0x1003
   */
  case class RedirectServer(newServerIp:String,portNumber:Int,message:String) extends  CommandCase{
    override val header: Int = 0x1003
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|%s|".format(newServerIp,portNumber,message),DefaultCharset)
  }
  /**
   * 0x1004
   */
  case class AuthFailed(message:String) extends  CommandCase{
    override val header: Int = 0x1004
    def body(implicit userData: Option[UserData]) = ByteString(message)
  }
  /**
   * 0x1005
   */
  case class InternalServerError(message:String) extends  CommandCase{
    override val header: Int = 0x1005
    def body(implicit userData: Option[UserData]) = ByteString(message)
  }
  /**
   * 0x1006
   */
  case class BannedUser(message:String) extends  CommandCase{
    override val header: Int = 0x1006
    def body(implicit userData: Option[UserData]) = ByteString(message)
  }
  /**
   * 0x1007
   */
  case class ClientVersionNotPermitted(requestVersion:String, protocolVersion:String, requiredAppVersion:String, requiredProtocolVersion:String,  updateLink:String) extends  CommandCase{
    override val header: Int = 0x1007
    def body(implicit userData: Option[UserData]) = ByteString("%s|%s|%s|%s|%s|".format(requestVersion,protocolVersion,requiredAppVersion,requiredProtocolVersion,updateLink))
  }
  /**
   * 0x1101
   */
  case class DisconnectedByAnotherConnection(reportCode:String) extends  CommandCase{
    override val header: Int = 0x1101
    def body(implicit userData: Option[UserData]) = ByteString(reportCode)
  }


  /**
   * 0x2001
   */
  case class SendChatMessage(address:String,messageJsonStr:String) extends  CommandCase{
    override val header: Int = 0x2001
    import AddressEncrypter._
    private def this(bodyParam:Array[String])(implicit userData: Option[UserData]) = this(AddressEncrypter.addressDecrypt(bodyParam(0))(userData), bodyParam(1))
    def this(body:ByteString)(implicit userData: Option[UserData])=this(body.decodeString(DefaultCharset).split('|'))
    def body(implicit userData: Option[UserData]) = ByteString("%s|%s|".format(addressEncrypt(address),messageJsonStr))
  }
  /**
   * 0x2002
   */
  //TODO
  case class SendChunkedMessageBegine() extends  CommandCase{
    override val header: Int = 0x2002
    def body(implicit userData: Option[UserData]) = ???
  }
  /**
   * 0x2003
   */
  //TODO
  case class SendChunkedMessageContinue() extends  CommandCase{
    override val header: Int = 0x2003
    def body(implicit userData: Option[UserData]) = ???
  }
  /**
   * 0x2004
   */
  //TODO
  case class SendChunkedMEssageEnd() extends  CommandCase{
    override val header: Int = 0x2004
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x2012
   */
  case class SendingMessageSuccessful(sendDateTime:Long) extends  CommandCase{
    override val header: Int = 0x2012
    def body(implicit userData: Option[UserData]) = ByteString(sendDateTime.toString)
  }

  /**
   * 0x2013
   */
  case class SendingMessageFailed(errorCode:Int, message:String) extends  CommandCase{
    override val header: Int = 0x2013
    def body(implicit userData: Option[UserData]) = ByteString("%d|%s|".format(errorCode,message))
  }

  /**
   * 0x2101
   */
  case class ReceiveMessageArrival(senderAddress:String,sendDateTime:Long, idx:Int, messageJson:String) extends  CommandCase{
    override val header: Int = 0x2101
    import AddressEncrypter._
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|%d|%s|".format(addressEncrypt(senderAddress),sendDateTime,idx,messageJson))
  }

  /**
   * 0x2102
   */
  //TODO
  case class ReceiveChunkedMessageBegin() extends  CommandCase{
    override val header: Int = 0x2102
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x2103
   */
  //TODO
  case class ReceiveChunkedMessageContinue() extends  CommandCase{
    override val header: Int = 0x2103
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x2104
   */
  //TODO
  case class ReceiveChunkedMessageEnd() extends  CommandCase{
    override val header: Int = 0x2104
    def body(implicit userData: Option[UserData]) = ???
  }


  /**
   * 0x2111
   */
  case class ReceivingMessageSuccessful(senderAddress:String, sendDatetime:Long, idx:Int) extends  CommandCase{
    override val header: Int = 0x2111
    import AddressEncrypter._
    private def this(bodyParam:Array[String])(implicit userData: Option[UserData]) = this(AddressEncrypter.addressDecrypt(bodyParam(0)), bodyParam(1).toLong, bodyParam(2).toInt)
    def this(body:ByteString)(implicit userData: Option[UserData])=this(body.decodeString(DefaultCharset).split('|'))
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|%d|".format(addressEncrypt(senderAddress),sendDatetime,idx),DefaultCharset)
  }

  /**
   * 0x2112
   */
  case class ReceivingMessageFailed(senderAddress:String, sendDatetime:Long, idx:Int) extends  CommandCase {
    override val header: Int = 0x2112
    import AddressEncrypter._
    private def this(bodyParam:Array[String])(implicit userData: Option[UserData]) = this(AddressEncrypter.addressDecrypt(bodyParam(0)), bodyParam(1).toLong, bodyParam(2).toInt)
    def this(body:ByteString)(implicit userData: Option[UserData])=this(body.decodeString(DefaultCharset).split('|'))
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|%d|".format(addressEncrypt(senderAddress),sendDatetime,idx),DefaultCharset)
  }

  /**
   * 0x3001
   */
  case class MessingMessageNotification(messageCount:Int) extends  CommandCase{
    override val header: Int = 0x3001
    def body(implicit userData: Option[UserData]) = ByteString(messageCount.toString)
  }

  /**
   * 0x3002
   */
  case class GetMissingMessageRequest() extends  CommandCase{
    override val header: Int = 0x3002
    def body(implicit userData: Option[UserData]) = ByteString()
  }

  /**
   * 0x3003
   */
  case class MissingMessage(senderAddress:String, sendDateTime:Long, idx:Int, messageJson:String) extends  CommandCase{
    override val header: Int = 0x3003
    import AddressEncrypter._
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|%d|%s|".format(addressEncrypt(senderAddress),sendDateTime,idx,messageJson))
  }

  /**
   * 0x3101
   */
  case class CheckMessageRead(address:String, lastCheckDateTime:Long) extends  CommandCase{
    override val header: Int = 0x3101
    import AddressEncrypter._
    private def this(bodyParam:Array[String])(implicit userData: Option[UserData]) = this(AddressEncrypter.addressDecrypt(bodyParam(0)), bodyParam(1).toLong)
    def this(body:ByteString)(implicit userData: Option[UserData])=this(body.decodeString(DefaultCharset).split('|'))
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|".format(addressEncrypt(address),lastCheckDateTime))
  }

  /**
   * 0x3102
   */
  case class LastMessageCheckDateTime(senderAddress:String, lastCheckDateTime:Long) extends  CommandCase{
    override val header: Int = 0x3102
    import AddressEncrypter._
    def body(implicit userData: Option[UserData]) = ByteString("%s|%d|".format(addressEncrypt(senderAddress),lastCheckDateTime))
  }

  /**
   * 0x4001
   */
  case class RandomMatchingEnqueue() extends  CommandCase{
    override val header: Int = 0x4001
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x4002
   */
  case class RandomMatchingDequeue() extends  CommandCase{
    override val header: Int = 0x4002
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x4101
   */
  case class EnqueueSuccessful() extends  CommandCase{
    override val header: Int = 0x4101
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x4111
   */
  case class EnqueueFailedAlreadInQueue() extends  CommandCase{
    override val header: Int = 4111
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x4112
   */
  case class EnqueueFailedMatchLimitExcessed() extends  CommandCase{
    override val header: Int = 0x4112
    def body(implicit userData: Option[UserData]) = ???
  }

  /**
   * 0x4201
   */
  case class MatchEstablished(virtualSenderAddress:String) extends  CommandCase{
    override val header: Int = 0x4201
    import AddressEncrypter._
    def body(implicit userData: Option[UserData]) = ByteString(addressEncrypt(virtualSenderAddress)
      ,DefaultCharset)
  }


  /**
   * 0x4202
   */
  case class MatchTimeout() extends  CommandCase{
    override val header: Int = 0x4201
    def body(implicit userData: Option[UserData]) = ByteString()
  }

  /**
   * 0x4301
   */
  case class RandomChatExit(virtualSenderAddress:String) extends  CommandCase{
    override val header: Int = 0x4301
    import AddressEncrypter._
    def this(body:ByteString)(implicit userData: Option[UserData])=this(AddressEncrypter.addressDecrypt(body.decodeString(DefaultCharset)))
    def body(implicit userData: Option[UserData]) = ByteString(addressEncrypt(virtualSenderAddress),DefaultCharset)
  }

}
