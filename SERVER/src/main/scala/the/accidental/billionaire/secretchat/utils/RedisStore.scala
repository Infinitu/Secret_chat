package the.accidental.billionaire.secretchat.utils

import com.redis.RedisClientPool
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Created by infinitu on 2015. 5. 16..
 */
object RedisStore {

  val config:Config = ConfigFactory.load().getConfig("redis")

  val redis_host = config.getString("host")
  val redis_port = config.getInt("port")
  val presence_collection_name = config.getString("presence_name")
  val randomRoom_collection_name = config.getString("randomroom_name")
  val friendsRequest_collection_name = config.getString("friends_request_name")

  val redisPool = new RedisClientPool(redis_host,redis_port)

}
