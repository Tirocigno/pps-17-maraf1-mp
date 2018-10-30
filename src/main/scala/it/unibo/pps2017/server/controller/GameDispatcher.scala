package it.unibo.pps2017.server.controller

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.database.RedisGameUtils
import it.unibo.pps2017.server.model.{Error, RouterResponse}

import scala.concurrent.ExecutionContext.Implicits.global

case class GameDispatcher() {

  val gameDatabaseUtils = RedisGameUtils()

  def getGame: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val gameId = ctx.request().getParam("gameId")


    gameId match {
      case Some(game) =>
        gameDatabaseUtils.getGame(game) map {
          case Some(fullGame) => res.sendResponse(fullGame)
          case None => res.setGenericError(Some("Error on game rescue!")).sendResponse(Error())
        }
      case None => res.setGenericError(Some("GameId not specified")).sendResponse(Error())
    }
  }


  def getLiveGames: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    res.sendResponse(gameDatabaseUtils.getLiveMatch)
  }

}
