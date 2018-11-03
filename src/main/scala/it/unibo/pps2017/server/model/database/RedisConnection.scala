package it.unibo.pps2017.server.model.database

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.server.model.database.RedisConnection.{REDIS_HOST, REDIS_PORT, REDIS_PW}
import redis.RedisClient
import redis.clients.jedis.Jedis

case class RedisConnection() {
  implicit val akkaSystem: ActorSystem = akka.actor.ActorSystem("RedisSystem", ConfigFactory.load("redisConf"))


  val redisHost: String = System.getenv("REDIS_HOST")
  val redisPort: String = System.getenv("REDIS_PORT")
  val redisPw: String = System.getenv("REDIS_PW")

  /**
    * Return an open database connection
    *
    * @return
    * an open connection to the database
    */
  def getDatabaseConnection: RedisClient = {

    if (redisHost == null || redisPort == null || redisPw == null) {
      RedisClient(REDIS_HOST, REDIS_PORT, REDIS_PW)
    } else {
      RedisClient(redisHost, redisPort.toInt, Some(redisPw))
    }
  }


  def getBlockingConnection: Jedis = {
    if (redisHost == null || redisPort == null || redisPw == null) {
      val db: Jedis = new Jedis(REDIS_HOST, REDIS_PORT)
      if (REDIS_PW.isDefined) {
        db.auth(REDIS_PW.get)
      }

      db
    } else {
      val db: Jedis = new Jedis(redisHost, redisPort.toInt)
      db.auth(redisPw)

      db
    }
  }
}

object RedisConnection {
  private var redisHost: String = "127.0.0.1"
  private var redisPort: Int = 6379
  private var redisPw: Option[String] = None

  def setRedisHost(value: String): Unit = redisHost = value

  def REDIS_HOST: String = redisHost

  def setRedisPort(value: Int): Unit = redisPort = value

  def REDIS_PORT: Int = redisPort

  def setRedisPw(value: String): Unit = redisPw = Some(value)

  def REDIS_PW: Option[String] = redisPw

}
