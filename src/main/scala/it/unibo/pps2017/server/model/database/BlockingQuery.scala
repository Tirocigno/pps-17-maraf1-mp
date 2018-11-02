package it.unibo.pps2017.server.model.database


import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BlockingQuery[T](body: Jedis => T, callback: T => Unit)(implicit db: Jedis = RedisConnection().getBlockingConnection) {
  var result: T = _
  Future {
    result = body(db)
  }.andThen { case _ => callback(result) }
    .andThen { case _ => closeConnection(db) }
}

object BlockingQuery {
  def apply[T](body: Jedis => T): BlockingQuery[T] = {
    new BlockingQuery[T](body, _ => {})
  }

  def withCallback[T](body: Jedis => T)(callback: T => Unit): BlockingQuery[T] = {
    new BlockingQuery[T](body, callback)
  }
}
