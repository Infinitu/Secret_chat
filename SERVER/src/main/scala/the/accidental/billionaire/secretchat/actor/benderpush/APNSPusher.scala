package the.accidental.billionaire.secretchat.actor.benderpush

import java.net.InetSocketAddress
import java.security.KeyStore
import javapns.Push
import javapns.notification.transmission.NotificationThreads
import javapns.notification.{PushNotificationPayload, AppleNotificationServerBasicImpl, Payload, PushNotificationManager}
import javax.net.ssl.{KeyManager, SSLContext, SSLEngine}

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import akka.stream.actor.{WatermarkRequestStrategy, RequestStrategy, ActorSubscriber, ActorPublisher}
import akka.stream.io._
import akka.stream.scaladsl.{Sink, Source, Flow, Tcp}
import akka.util.ByteString
import com.typesafe.config.{ConfigFactory, Config}
/**
 * Created by infinitu on 2015. 5. 29..
 */
object APNSPusher{
  val config:Config = ConfigFactory.load().getConfig("apns")
  val host = config.getString("host")
  val port = config.getInt("port")
  val auth = config.getString("auth_file")
  val pass = config.getString("auth_path")
  case class ApnsPush(token:String,alert:String,badge:Int)
}

class APNSPusher extends Actor{
  import APNSPusher._
  val thread = new NotificationThreads(new AppleNotificationServerBasicImpl(auth,pass,"PKCS12",host,port),3)
  override def receive: Receive = {
    case ApnsPush(token,alert,badge)=>
      val payload = new PushNotificationPayload()
      payload.addAlert(alert)
      payload.addBadge(badge)
      thread.add(payload,token)
    case _=>
  }
}
