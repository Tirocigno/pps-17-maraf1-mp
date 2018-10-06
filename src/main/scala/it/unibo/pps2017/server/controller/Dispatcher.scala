package it.unibo.pps2017.server.controller

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.model.{ConsumeBeforeRes, GET, POST}
import Utility._


object Utility {
  val applicationJson: String = "application/json"
  val USER = "user:"
  var HOST: String = "localhost"
  var PORT: Int = 4700
  var PASSWORD: Option[String] = Some("")
  val RESULT = "result"
}


class Dispatcher extends ScalaVerticle {

  override def start(): Unit = {


    val router = Router.router(vertx)

    GET(router, "/", hello)

    GET(router, "/game/:gameId", getGame)

    POST(router, "/foundGame", foundGame)


    vertx.createHttpServer()
      .requestHandler(router.accept _).listen(PORT)

  }

  /**
    * Schermata di "Welcome"
    */
  private val hello: (RoutingContext, JsonObject, ConsumeBeforeRes) => Unit = (routingContext, data, res) => {
    res.initialize(1)

    data.put(RESULT, "Hello to everyone")
    res.consume()
  }

  /**
    * Respond to GET /game/:gameId
    *
    */
  private val getGame: (RoutingContext, JsonObject, ConsumeBeforeRes) => Unit = (routingContext, data, res) => {
    res.initialize(1)

    val gameId = routingContext.request().getParam("gameId")

    gameId match {
      case Some(game) => data.put("gameId", "You write " + game)
      case None => data.put("gameId", "you write nothing")
    }

    res.consume()
  }

  /**
    * Respond to GET /foundGame
    *
    */
  private val foundGame: (RoutingContext, JsonObject, ConsumeBeforeRes) => Unit = (routingContext, data, res) => {
    res.initialize(1)

    val player = routingContext.request().getParam("me")
    val friend = routingContext.request().getParam("partner")



    player match {
      case Some(user) => data.put("you", "You are " + user)
      case None => data.put("error", "No user found")
    }

    res.consume()
  }
}


