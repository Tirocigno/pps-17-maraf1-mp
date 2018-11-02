package it.unibo.pps2017.server.controller

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.database.RedisGameUtils
import it.unibo.pps2017.server.model.{RouterResponse, SavedMatches}

import scala.concurrent.ExecutionContext.Implicits.global

case class GameDispatcher() {

  val gameDatabaseUtils = RedisGameUtils()

  def getGame: (RoutingContext, RouterResponse) => Unit = (ctx, res) => {
    val gameId = ctx.request().getParam("gameId")


    gameId match {
      case Some(game) =>
        gameDatabaseUtils.getGame(game, _ map {
          case Some(fullGame) => res.sendResponse(fullGame)
          case None => errorHandler(res, "Error on game rescue!")
        })
      case None => errorHandler(res, "GameId not specified")
    }
  }


  def getLiveGames: (RoutingContext, RouterResponse) => Unit = (_, res) => {
    gameDatabaseUtils.getLiveMatches(res.sendResponse)
  }


  def getSavedMatches: (RoutingContext, RouterResponse) => Unit = (_, res) => {
    gameDatabaseUtils.getSavedMatches(res.sendResponse, cause => errorHandler(res, cause.getMessage))
  }

}
