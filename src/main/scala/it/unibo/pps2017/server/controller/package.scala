package it.unibo.pps2017.server

import io.vertx.scala.core.MultiMap
import it.unibo.pps2017.server.model.{Error, RouterResponse}
import redis.RedisClient

import scala.collection.mutable

package object controller {
  implicit def formParamsToMap(formParams: MultiMap): Map[String, String] = {
    val params: mutable.HashMap[String, String] = mutable.HashMap()

    formParams.names foreach { name => params += (name -> formParams.get(name).get) }

    params.toMap
  }


  def closeDatabaseConnection(db: RedisClient): () => Unit = () => {
    db.quit()
  }


  def errorHandler(res: RouterResponse, msg: String):Unit = {
    res.setGenericError(Some(msg)).sendResponse(Error())
  }
}
