package it.unibo.pps2017.server.model.database

import it.unibo.pps2017.server.model.database.base.DatabaseInterface

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}



/**
  * This class encapsulate some method for manage generic keys in the database
  */
case class RedisUtils() extends DatabaseInterface {

  override def getRanking(onSuccess: Seq[(String, Double)] => Unit, onFail: Throwable => Unit, from: Option[Long] = None, to: Option[Long] = None): Unit = {

    val fromRange: Long = from.getOrElse(0)
    val toRange: Long = to.getOrElse(-1)

    Query(db =>
      db.zrevrangeWithscores(RANKING_KEY, fromRange, toRange)
        .onComplete {
          case Success(res) =>
            onSuccess(res.map(e => (e._1.utf8String, e._2)))
          case Failure(cause) =>
            onFail(cause)
        }
    )
  }
}