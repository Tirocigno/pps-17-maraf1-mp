package it.unibo.pps2017.server.controller


import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.controller.Dispatcher._
import it.unibo.pps2017.server.model._


object Dispatcher {
  val applicationJson: String = "application/json"
  val USER = "user:"
  var HOST: String = "localhost"
  var PORT: Int = 4700
  var PASSWORD: Option[String] = Some("")
  val RESULT = "result"
  val TIMEOUT = 1000
}


class Dispatcher extends ScalaVerticle {

  override def start(): Unit = {


    val router = Router.router(vertx)

    GET(router, "/", hello)

    GET(router, "/game/:gameId", getGame)

    GET(router, "/error", responseError)

    POST(router, "/foundGame", foundGame)


    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(TIMEOUT)


    var port = 4700

    if (System.getenv("PORT") != null) port = System.getenv("PORT").toInt

    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(port)

  }

  /**
    * Welcome response.
    */
  private val hello: (RoutingContext, RouterResponse) => Unit = (_, res) => {
    res.sendResponse(Message("Hello to everyone"))
  }

  /**
    * Respond to GET /game/:gameId
    *
    */
  private val getGame: (RoutingContext, RouterResponse) => Unit = (routingContext, res) => {
    val gameId = routingContext.request().getParam("gameId")


    gameId match {
      case Some(game) => res.sendResponse(Game("You write " + game))
      case None => res.sendResponse(Error(Some("you write nothing")))
    }
  }

  /**
    * Respond to GET /foundGame
    *
    */
  private val foundGame: (RoutingContext, RouterResponse) => Unit = (routingContext, res) => {

    val player = routingContext.request().getParam("me")
    val friend = routingContext.request().getParam("partner")

  }

  /**
    * Respond with a generic error message
    *
    */
  private val responseError: (RoutingContext, RouterResponse) => Unit = (_, res) => {

    res.setGenericError(Some("Error"))
      .sendResponse(Error())
  }
}
