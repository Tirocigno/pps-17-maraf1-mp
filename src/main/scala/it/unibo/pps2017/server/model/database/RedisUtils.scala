package it.unibo.pps2017.server.model.database

import redis.RedisClient
import redis.api.Limit

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

sealed trait DatabaseUtils {

  /**
    * Retrieve the ranking from the database.
    *
    * @param onSuccess
    * On query success.
    * @param onFail
    * error handler.
    * @param from
    * starting position.
    * @param to
    * ending position.
    */
  def getRanking(onSuccess: Seq[(String, Double)] => Unit,
                 onFail: Throwable => Unit,
                 from: Option[Long],
                 to: Option[Long]): Unit
}

/**
  * This class encapsulate some method for manage generic keys in the database
  */
case class RedisUtils() extends DatabaseUtils {

  override def getRanking(onSuccess: Seq[(String, Double)] => Unit, onFail: Throwable => Unit, from: Option[Long] = None, to: Option[Long] = None): Unit = {
    val db: RedisClient = RedisConnection().getDatabaseConnection
    val fromRange: Long = from.getOrElse(0)
    val toRange: Long = to.getOrElse(-1)

    
    db.zrevrangeWithscores(RANKING_KEY, fromRange, toRange)
      .onComplete {
        case Success(res) =>
          db.quit()
          onSuccess(res.map(e => (e._1.utf8String, e._2)))
        case Failure(cause) =>
          db.quit()
          onFail(cause)
      }
  }
}