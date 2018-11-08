package it.unibo.pps2017.server.model.database


import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Create a space for executing blocking Redis commands.
  * This class use Jedis so the body is runned in a future but all DB query are blocking.
  * Give to the body a connection to the db and after all operations close the connection.
  *
  * @param body
  * Function
  * @param callback
  * Function that takes in input the body's return value.
  * @param db
  * Database connection.
  */
class BlockingQuery[T](body: Jedis => T, callback: T => Unit)(implicit db: Jedis = RedisConnection().getBlockingConnection) {
  var result: T = _
  Future {
    result = body(db)
  }.andThen { case _ => callback(result) }
    .andThen { case _ => db.closeConnection() }
}

object BlockingQuery {
  def apply[T](body: Jedis => T): BlockingQuery[T] = {
    new BlockingQuery[T](body, _ => {})
  }

  def withCallback[T](body: Jedis => T)(callback: T => Unit): BlockingQuery[T] = {
    new BlockingQuery[T](body, callback)
  }
}
