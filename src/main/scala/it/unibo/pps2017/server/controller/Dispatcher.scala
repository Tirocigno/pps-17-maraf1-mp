
package it.unibo.pps2017.server.controller


import akka.actor.{ActorRef, ActorSystem, Props}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterServerAPI
import it.unibo.pps2017.server.actor.{LobbyActor, MultiPlayerMsg, SinglePlayerMsg}
import it.unibo.pps2017.server.controller.Dispatcher.{PORT, TIMEOUT}
import it.unibo.pps2017.server.model.ServerApi.{ErrorRestAPI$, FoundGameRestAPI$, GameRestAPI$, HelloRestAPI$}
import it.unibo.pps2017.server.model._
import org.json4s._
import org.json4s.jackson.Serialization.read

import scala.concurrent.duration._

object Dispatcher {
  val applicationJson: String = "application/json"
  val USER = "user:"
  var HOST: String = "localhost"
  val PORT: Int = 4700
  var PASSWORD: Option[String] = Some("")
  val RESULT = "result"
  val TIMEOUT = 1000
  val DISCOVERY_URL: String = "localhost"
  val DISCOVERY_PORT: Int = 2000
  val VERTX = Vertx.vertx()
}


case class Dispatcher(actorSystem: ActorSystem) extends ScalaVerticle {

  implicit val akkaSystem: ActorSystem = actorSystem
  implicit val formats: DefaultFormats.type = DefaultFormats


  val lobbyManager: ActorRef = akkaSystem.actorOf(Props[LobbyActor])

  override def start(): Unit = {


    val router = Router.router(vertx)

    ServerApi.values.map({
      case api@HelloRestAPI$ => api.asRequest(router, hello)
      case api@ErrorRestAPI$ => api.asRequest(router, responseError)
      case api@GameRestAPI$ => api.asRequest(router, getGame)
      case api@FoundGameRestAPI$ => api.asRequest(router, foundGame)
      case api@_ => api.asRequest(router, (_, res) => res.setGenericError(Some("RestAPI not founded.")).sendResponse(Error()))
    })


    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(TIMEOUT)


    var port = PORT

    if (System.getenv("PORT") != null) port = System.getenv("PORT").toInt

    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(port)


    akkaSystem.scheduler.scheduleOnce(5 second) {
      PostRequest(Dispatcher.DISCOVERY_URL, RegisterServerAPI.path, {
        case Some(res) => try {
          val msgFromDiscovery = read[Message](res)

          println("Discovery registration response: " + msgFromDiscovery.message)
        } catch {
          case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
        }
        case None => println("No response from the discovery")
      }, cause => {
        println("Error on the discovery registration! \nDetails: " + cause.getMessage)
      }, None, Some(Dispatcher.DISCOVERY_PORT))
    }
  }

  /**
    * Welcome response.
    */
  private val hello: (RoutingContext, RouterResponse) => Unit = (_, res) => {
    res.sendResponse(Message("Hello to everyone"))
  }

  /**
    * Respond to GET /game/:gameId
    * TODO / Pending
    */
  private val getGame: (RoutingContext, RouterResponse) => Unit = (routingContext, res) => {
    val gameId = routingContext.request().getParam("gameId")


    gameId match {
      case Some(game) =>
        val team1: Side = Side(Seq("player1", "player2"))
        val team2: Side = Side(Seq("player3", "player4"))
        val gameSet: GameSet = GameSet(Seq("Card1", "Card2"),
          Seq("Card3", "Card5"),
          Seq("Card6", "Card7"),
          Seq("Card8", "Card9"), Seq("PLAY CARD", "SET BRISCOLA"))
        val gameHistory: GameHistory = GameHistory(game, Seq(team1, team2), gameSet)
        res.sendResponse(gameHistory)
      case None => res.sendResponse(Error(Some("you write nothing")))
    }
  }


  /**
    * Respond to POST /foundGame
    *
    */
  private val foundGame: (RoutingContext, RouterResponse) => Unit = (routingContext, res) => {
    val params = routingContext.request().formAttributes()

    val player = params.get("me")
    val friend = params.get("partner")

    val gameFoundEvent: String => Unit = gameId => {
      res.sendResponse(GameFound(gameId))
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
