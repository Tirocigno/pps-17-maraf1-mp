package it.unibo.pps2017.server.model.database

import redis.RedisClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Query[T](body: RedisClient => T, callback: T => Unit)(implicit db: RedisClient = RedisConnection().getDatabaseConnection) {
  var result: T = _
  Future {
    result = body(db)
  }.andThen { case _ => callback(result) }
    .andThen { case _ => closeConnection(db) }
}

object Query {
  def apply[T](body: RedisClient => T): Query[T] = {
    new Query[T](body, _ => {})
  }

  def withCallback[T](body: RedisClient => T)(callback: T => Unit): Query[T] = {
    new Query[T](body, callback)
  }
}
