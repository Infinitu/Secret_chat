package the.accidental.billionaire.secretchat.protocol

import the.accidental.billionaire.secretchat.security.UserData
import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 2015. 4. 6..
 */
trait BodyInterpreter {this: ReceivePipeline =>
  val systemChatPattern = "^(system_)(.+)$".r
  val randomChatPattern = "^(random_)(.+)$".r
  implicit var userData: Option[UserData] = None
  pipelineInner(receiveTLVMessage)


  def receiveTLVMessage(inner:Receive):Receive ={
    case Command(0x0001,length,body)=> // ping
      inner(Ping)
    case Command(0x0002,length,body)=> // pong
      inner(Pong)
    case Command(0x1001,length,body)=>
      inner(new SessionLoginRequest(body))
    case Command(0x2001,length,body)=>
      val msg = new SendChatMessagePlain(body)
      inner apply {
        msg.address match {
          case systemChatPattern(_, addr) =>
            new SendSystemChatMessage(addr, msg.messageJsonStr)
          case randomChatPattern(_, addr) =>
            new SendRandomChatMessage(addr, msg.messageJsonStr)
          case _ =>
            new SendChatMessage(msg)
        }
      }
    case Command(0x2111,length,body)=>
      inner(new ReceivingMessageSuccessful(body))
    case Command(0x2112,length,body)=>
      inner(new ReceivingMessageFailed(body))
    case Command(0x3101,length,body)=>
      inner(new CheckMessageRead(body))
    case Command(0x4301,length,body)=>
      inner(new RandomChatExit(body))
    case notDefined=>
      inner(notDefined)
  }

  def writeToConnection(message:CommandCase) = {
    connection ! Command2Write(message)
  }
}
