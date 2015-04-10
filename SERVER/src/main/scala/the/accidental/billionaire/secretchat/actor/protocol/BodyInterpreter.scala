package the.accidental.billionaire.secretchat.actor.protocol

import the.accidental.billionaire.secretchat.actor.protocol._
import the.accidental.billionaire.secretchat.actor.security.UserData
import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 2015. 4. 6..
 */
trait BodyInterpreter {this: ReceivePipeline =>

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
      inner(new SendChatMessage(body))
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
