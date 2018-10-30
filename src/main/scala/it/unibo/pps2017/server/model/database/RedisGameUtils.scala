package it.unibo.pps2017.server.model.database

import it.unibo.pps2017.server.model.GameType.GameType
import it.unibo.pps2017.server.model.{Game, LiveGame, Matches, Side}
import org.json4s.jackson.Serialization.{read, write}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


sealed trait GameDatabaseUtils {
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
    * @return
    * the specified game.
    */
  def getGame(gameId: String): Future[Option[Game]]


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
    * Return a list of the match in execution on the server.
    *
    * @return
    * a list of the match in execution on the server.
    *
    */
  def getLiveMatch: Matches
}

class RedisGameUtils extends GameDatabaseUtils {
  /**
    * Storage the game in the database.
    *
    * @param gameId
    * Game identifier.
    * @param game
    * Full game.
    */
  override def saveGame(gameId: String, game: Game): Unit = {
    val db = RedisConnection().getDatabaseConnection

    db.set(getGameHistoryKey(gameId), write(game)).onComplete(_ => {
      db.quit()
    })
  }

  /**
    * Return the full specified game.
    *
    * @param gameId
    * Searched game id.
    * @return
    * the specified game.
    */
  override def getGame(gameId: String): Future[Option[Game]] = {
    val db = RedisConnection().getDatabaseConnection

    db.get(getGameHistoryKey(gameId)).map {
      case Some(game) => db.quit(); Some(read[Game](game.utf8String))
      case None => db.quit(); None
    }
  }

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
  override def signNewGame(gameId: String, team1: Side, team2: Side, gameType: GameType): Unit = {
    val db = RedisConnection().getDatabaseConnection
    val map: mutable.HashMap[String, String] = mutable.HashMap()

    map += TEAM1_KEY -> (team1.members.head + " : " + team1.members.last)
    map += TEAM2_KEY -> (team2.members.head + " : " + team2.members.last)

    db.hmset(getInGameKey(gameId, gameType), map.toMap).onComplete {
      case Success(res) =>
        if (res) {
          db.expire(getInGameKey(gameId, gameType), GAME_TIME_TO_LIVE).onComplete(_ => db.quit())
        }
      case Failure(_) =>
    }
  }

  /**
    * Set the game as ended.
    *
    * @param gameId
    * game id.
    * @param gameType
    * type of game.
    */
  override def setGameEnd(gameId: String, gameType: GameType): Unit = {
    val db = RedisConnection().getDatabaseConnection

    db.del(getInGameKey(gameId, gameType)).onComplete(_ => db.quit())
  }

  /**
    * Return a list of the match in execution on the server.
    *
    * @return
    * a list of the match in execution on the server.
    *
    */
  override def getLiveMatch: Matches = {
    val games: ListBuffer[LiveGame] = ListBuffer()
    val db = RedisConnection().getBlockingConnection

    db.keys(getLiveKeyPattern).forEach(key => {
      val teams = db.hgetAll(key)

      games += LiveGame(getGameIdFromLiveGameKey(key),
        convertToSide(teams.get(TEAM1_KEY)),
        convertToSide(teams.get(TEAM2_KEY)),
        getGameTypeFromLiveGameKey(key))
    })


    db.disconnect()

    Matches(games)
  }


  private def getGameIdFromLiveGameKey(key: String): String = {
    key.split(KEY_SPLITTER)(1).trim
  }

  private def getGameTypeFromLiveGameKey(key: String): String = {
    key.split(KEY_SPLITTER)(3).trim
  }

  private def convertToSide(value: String): Side = {
    val members = value.split(KEY_SPLITTER)
    Side(Seq(members(0).trim, members(1).trim))
  }
}

object RedisGameUtils {

  def apply(): RedisGameUtils = new RedisGameUtils()
}
