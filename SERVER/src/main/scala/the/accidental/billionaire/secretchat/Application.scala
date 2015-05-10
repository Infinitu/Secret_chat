package the.accidental.billionaire.secretchat

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.{ConfigFactory, Config}
import the.accidental.billionaire.secretchat.actor.Server.Start
import the.accidental.billionaire.secretchat.actor.{MissingMessageDispatcher, MessageDispatcher, Server, UserService}

/**
 * Created by infinitu on 2015. 4. 12..
 */
object Application {
  def main(args:Array[String]): Unit ={

    val config:Config = ConfigFactory.load().getConfig("secretchat.tcp")
    val port = config.getInt("port")
    val system = ActorSystem("secretchat")

    system.actorOf(Props[MessageDispatcher],MessageDispatcher.actorPath)
    system.actorOf(Props[UserService],UserService.actorPath)
    system.actorOf(Props[MissingMessageDispatcher],MissingMessageDispatcher.actorPath)
    val server = system.actorOf(Props[Server],"server")
    server ! Start(port)
  }
}
