package it.unibo.pps2017.server.model.database.base

import it.unibo.pps2017.server.model.GameType.GameType
import it.unibo.pps2017.server.model.{Game, Matches, SavedMatches, Side}

import scala.concurrent.Future

trait GameDatabaseInterface {
  /**
    * Storage the game in the database.
    *
    * @param gameId
    * Game identifier.
    * @param game
    * Full game.
    */
  def saveGame(gameId: String, game: Game): Unit

  /**
    * Return the full specified game.
    *
    * @param gameId
    * Searched game id.
    * @param onComplete
    * on query success handler
    * @return
    * the specified game.
    */
  def getGame(gameId: String, onComplete: Future[Option[Game]] => Unit): Unit


  /**
    * Register a game in the database.
    *
    * @param gameId
    * game id.
    * @param team1
    * first team.
    * @param team2
    * second team.
    * @param gameType
    * type of game.
    */
  def signNewGame(gameId: String, team1: Side, team2: Side, gameType: GameType): Unit


  /**
    * Set a game as ended.
    *
    * @param gameId
    * game id.
    * @param gameType
    * type of game.
    */
  def setGameEnd(gameId: String, gameType: GameType): Unit


  /**
    * Search a list of match in execution on the server.
    *
    * @param onComplete
    * on query success handler
    */
  def getLiveMatches(onComplete: Matches => Unit): Unit


  /**
    * Search a list of played matches.
    *
    */
  def getSavedMatches(onSuccess: SavedMatches => Unit, onFail: Throwable => Unit): Unit

}
