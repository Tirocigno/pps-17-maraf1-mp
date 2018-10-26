package it.unibo.pps2017.server.model.database

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.server.model.{DEFAULT_REDIS_HOST, DEFAULT_REDIS_PORT, DEFAULT_REDIS_PW}
import redis.RedisClient

case class RedisConnection() {
  implicit val akkaSystem: ActorSystem = akka.actor.ActorSystem("RedisSystem", ConfigFactory.load("redisConf"))


  val redisHost: String = System.getenv("REDIS_HOST")
  val redisPort: String = System.getenv("REDIS_PORT")
  val redisPw: String = System.getenv("REDIS_PW")

  private var db: RedisClient = _


  /**
    * Return an open database connection
    *
    * @return
    * an open connection to the database
    */
  def getDatabaseConnection: RedisClient = {
    if (db != null) {
      db
    } else {
      if (redisHost == null || redisPort == null || redisPw == null) {
        db = RedisClient(DEFAULT_REDIS_HOST, DEFAULT_REDIS_PORT, DEFAULT_REDIS_PW)
      } else {
        db = RedisClient(redisHost, redisPort.toInt, Some(redisPw))
      }

      db
    }
  }
}
