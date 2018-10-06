package it.unibo.pps2017.server.model

import io.vertx.core.http.HttpMethod
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.ext.web.{Router, RoutingContext}


/**
  * Interfaccio per la gestione di una richiesta proveniente dal server di Vertx.
  */
trait Request {
  def router: Router

  def url: String

  def method: HttpMethod

  def handle: (RoutingContext, RouterResponse) => Unit

  def handler(): Unit = {
    router.route(method, url).produces("application/json").handler(routingContext => {
      val res = RouterResponse(routingContext)
      handle(routingContext, res)
    })
  }
}


/**
  * Tipo di richiesta GET
  *
  * @param router
  * Oggetto che si occupa del routing
  * @param url
  * URL da gestire
  * @param handle
  * Funzione che si occupa di gestire la richiesta e di produrre una risposta
  */
case class GET(override val router: Router,
               override val url: String,
               override val handle: (RoutingContext, RouterResponse) => Unit) extends Request {
  override val method = HttpMethod.GET

  handler()
}

/**
  * Tipo di richiesta POST
  *
  * @param router
  * Oggetto che si occupa del routing
  * @param url
  * URL da gestire
  * @param handle
  * Funzione che si occupa di gestire la richiesta e di produrre una risposta
  */
case class POST(override val router: Router,
                override val url: String,
                override val handle: (RoutingContext, RouterResponse) => Unit) extends Request {
  override val method = HttpMethod.POST

  handler()
}




object ResponseStatus {

  val OK_CODE: Int = 200
  val EXCEPTION_CODE: Int = 409

  sealed trait HeaderStatus

  case object OK extends HeaderStatus

  case object ResponseException extends HeaderStatus


  /**
    * This method is used to get all the available seeds
    *
    * @return a Iterable containing all the available seeds.
    */
  def values: Iterable[HeaderStatus] = Iterable(OK, ResponseException)

}


