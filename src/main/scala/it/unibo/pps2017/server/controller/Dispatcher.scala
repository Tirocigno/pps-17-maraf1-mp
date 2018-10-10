
package it.unibo.pps2017.server.controller


import akka.actor.{ActorRef, ActorSystem, Props}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.commons.API
import it.unibo.pps2017.commons.API.{ErrorAPI, FoundGameAPI, GameAPI, HelloAPI}
import it.unibo.pps2017.server.actor.{LobbyActor, MultiPlayerMsg, SinglePlayerMsg}
import it.unibo.pps2017.server.controller.Dispatcher.{PORT, TIMEOUT}
import it.unibo.pps2017.server.model._

import scala.concurrent.duration._

object Dispatcher {
  val applicationJson: String = "application/json"
  val USER = "user:"
  var HOST: String = "localhost"
  val PORT: Int = 4700
  var PASSWORD: Option[String] = Some("")
  val RESULT = "result"
  val TIMEOUT = 1000
  val DISCOVERY_URL: String = ""
  val DISCOVERY_PORT: Int = 0
}


class Dispatcher extends ScalaVerticle {

  implicit val akkaSystem: ActorSystem = akka.actor.ActorSystem()

  val lobbyManager: ActorRef = ActorSystem("Lobby").actorOf(Props[LobbyActor])

  override def start(): Unit = {


    val router = Router.router(vertx)

    API.values.map({
      case api@HelloAPI => api.asRequest(router, hello)
      case api@ErrorAPI => api.asRequest(router, responseError)
      case api@GameAPI => api.asRequest(router, getGame)
      case api@FoundGameAPI => api.asRequest(router, foundGame)
      case api@_ => api.asRequest(router, (_, res) => res.setGenericError(Some("API not founded.")).sendResponse(Error()))
    })


    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(TIMEOUT)


    var port = PORT

    if (System.getenv("PORT") != null) port = System.getenv("PORT").toInt

    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(port)


    akkaSystem.scheduler.schedule(5 seconds, 30 seconds)({
      val currentGame: String = "0"

      GETReq(Dispatcher.DISCOVERY_URL, Dispatcher.DISCOVERY_PORT, "/addServer", _ => {}, failRes => {
        println("Error on talk with the DISCOVERY. Messasge: " + failRes.getMessage)
      }, Map[String, String]("nMatch" -> currentGame),vertx = vertx)
    }
    )

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
    * Respond to POST /foundGame
    *
    */
  private val foundGame: (RoutingContext, RouterResponse) => Unit = (routingContext, res) => {
    val params = routingContext.queryParams()

    val player = params.get("me")
    val friend = params.get("partner")

    val gameFoundEvent: String => Unit = gameId => {
      res.sendResponse(Game(gameId))
    }

    player match {
      case Some(id) =>
        friend match {
          case Some(idPartner) =>
            lobbyManager ! MultiPlayerMsg(id.toString, idPartner.toString, gameFoundEvent)
          case None =>
            lobbyManager ! SinglePlayerMsg(id.toString, gameFoundEvent)
        }
      case None =>
        res.setGenericError(Some("Id player not specified in the request")).sendResponse(Error())
    }
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
