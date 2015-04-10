package the.accidental.billionaire.secretchat.actor.protocol

import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 2015. 4. 6..
 */
trait BodyInterpreter {this: ReceivePipeline =>

  def receiveTCP(inner:Receive):Receive ={
    case Command(0x0001,length,body)=> // ping
      Ping()
    case Command(0x1001,length,body)=>
      new SessionLoginRequest(body)
    case Command(0x2001,length,body)=>
      new SendChatMessage(body)
    case Command(0x2111,length,body)=>
      new ReceivingMessageSuccessful(body)
    case Command(0x2112,length,body)=>
      new ReceivingMessageFailed(body)
    case Command(0x3101,length,body)=>
      new CheckMessageRead(body)
    case Command(0x4301,length,body)=>
  }
}
