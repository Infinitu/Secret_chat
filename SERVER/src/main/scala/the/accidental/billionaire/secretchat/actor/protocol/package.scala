package the.accidental.billionaire.secretchat.actor

import akka.util.ByteString

/**
 * Created by infinitu on 15. 4. 3..
 */
package object protocol {

  case class Command(header:Int, length:Int, body:ByteString){
    val complete =
      if(length == body.length)
        Some(this)
      else if(length < body.length)
        Some(Command(header,length,body.take(length)))
      else
        None
    def ++(additionalBody:ByteString): Command =
      Command(header,length,body++additionalBody)

    def overplusBody:Option[ByteString]=
      if(body.length > length)
        Some(body.drop(length))
      else
        None
  }
}
