package the.accidental.billionaire.secretchat.protocol

import akka.io.Tcp.Received
import akka.util.ByteString
import the.accidental.billionaire.secretchat.utils.ReceivePipeline

/**
 * Created by infinitu on 15. 4. 3..
 */

trait TLVInterpreter {this: ReceivePipeline =>

  var fragment:Option[Command] = None
  var smallFragment:Option[ByteString] = None

  pipelineOuter(receiveTCP)

  def receiveTCP(inner:Receive):Receive = {
    case Received(data) if fragment.isDefined=>
      newCommand(fragment.get ++ data)(inner)

    case Received(data) if smallFragment.isDefined=>
      val preData = smallFragment.get
      smallFragment = None
      receiveTCP(inner)(Received(preData++data))

    case Received(data) if data.length == 0=>
    case Received(data) if data.length < 6=>
      smallFragment = Some(data)
    case Received(data) =>
      val header = ((data(0)&0xff) << 8) | (data(1)&0xff)
      val length = ((data(2)&0xff) << 24) | ((data(3)&0xff) << 16) | ((data(4)&0xff) << 8) | (data(5)&0xff)
      val body = data.drop(6)
      newCommand(Command(header,length,body))(inner)
    case unknown=>
      inner apply unknown;
  }

  def newCommand(command:Command)(inner:Receive): Unit ={
    if(command.complete.isDefined){
      fragment = None
      inner(command.complete.get)
      command.overplusBody.foreach{over=> receiveTCP(inner)(Received(over))}
    }
    else
      fragment = Some(command)
  }
}
