package the.accidental.billionaire.secretchat.actor

import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem, Actor}
import akka.actor.Actor.Receive
import akka.io.Tcp.{Register, Connected, Bind}
import akka.io.{Tcp, IO}
import akka.io.Inet.SocketOption

import scala.collection.immutable

/**
 * Created by infinitu on 15. 4. 1..
 */
object Server {

  def actorSystem = ActorSystem("secretchat")

  case class Start(port: Int,
                   backlog: Int = 10000,
                   options: immutable.Traversable[SocketOption] = Nil,
                   pullMode: Boolean = false)

  case class Stop()

}
class Server extends Actor{
  import Server._
  import context.system
  val io = IO(Tcp)


  override def receive= {
    case Start(port, backlog, options, pullMode) =>
      io ! Bind(self, new InetSocketAddress(port), backlog, options, pullMode)
      context become started
  }

  def started:Receive ={
    case Stop()=>
    //stop
    case c @ Connected(remote, local) ⇒
      val connection = sender()
      val handler = context.actorOf(Props(classOf[TcpHandler],connection))
      connection ! Register(handler)
  }
}
