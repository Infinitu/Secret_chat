package the.accidental.billionaire.secretchat.protocol

import akka.actor.Cancellable
import scala.concurrent.duration._
import the.accidental.billionaire.secretchat.utils.ReceivePipeline
import scala.concurrent.ExecutionContext.Implicits._

/**
 * Created by infinitu on 2015. 4. 10..
 */
object PingResponder{
  case object PingTimeout
  case object sendPing
}
trait PingResponder {this:ReceivePipeline with BodyInterpreter=>

  var schedule:Option[Cancellable] = None
  val timeout = 10 seconds //todo Configable

  pipelineInner(receivePingPong)



  def receivePingPong(inner:Receive):Receive ={
    case Ping=> sendPong()
    case Pong=> pingTimerReset()
    case PingResponder.sendPing => sendPing()
    case notPing=> inner(notPing)
  }

  private lazy val pingTimeoutRunnable:Runnable = new Runnable() { def run() = pingTimeout() }
  def sendPing():Unit ={
    if(schedule isDefined)
      return
    writeToConnection(Ping)
    schedule = Some(context.system.scheduler.scheduleOnce(timeout,pingTimeoutRunnable))
  }

  def sendPong():Unit ={
    writeToConnection(Pong)
  }

  def pingTimerReset(): Unit ={
    if(schedule.isEmpty)
      return

    schedule.get.cancel()
    schedule = None
  }

  def pingTimeout()

}
