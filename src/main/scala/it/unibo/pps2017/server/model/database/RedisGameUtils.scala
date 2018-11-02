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
  def getLiveMatch(onComplete: Matches => Unit): Unit


  /**
    * Return a list of the played matches.
    *
    * @return
    * a list of the played matches.
    */
  def getSavedMatch(onSuccess: Seq[String] => Unit, onFail: Throwable => Unit): Unit
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
    Query(db =>
      db.set(getGameHistoryKey(gameId), write(game))
    )
  }


  /**
    * Return the full specified game.
    *
    * @param gameId
    * Searched game id.
    * @return
    * the specified game.
    */
  override def getGame(gameId: String, onComplete: Future[Option[Game]] => Unit): Unit = {
    Query.withCallback(db =>
      db.get(getGameHistoryKey(gameId)).map {
        case Some(game) => Some(read[Game](game.utf8String))
        case None => None
      }
    )(onComplete)
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
    val map: mutable.HashMap[String, String] = mutable.HashMap()

    map += TEAM1_KEY -> (team1.members.head + " : " + team1.members.last)
    map += TEAM2_KEY -> (team2.members.head + " : " + team2.members.last)

    Query(db =>
      db.hmset(getInGameKey(gameId, gameType), map.toMap).onComplete {
        case Success(res) =>
          if (res) {
            db.expire(getInGameKey(gameId, gameType), GAME_TIME_TO_LIVE)
          }
        case Failure(_) =>
      }
    )
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
    Query(db =>
      db.del(getInGameKey(gameId, gameType))
    )
  }

  /**
    * Search a list of match in execution on the server.
    *
    * @param onComplete
    * on query success handler
    */
  override def getLiveMatch(onComplete: Matches => Unit): Unit = {
    val games: ListBuffer[LiveGame] = ListBuffer()

    BlockingQuery.withCallback(db => {
      db.keys(getLiveKeyPattern).forEach(key => {
        val teams = db.hgetAll(key)

        games += LiveGame(getGameIdFromLiveGameKey(key),
          convertToSide(teams.get(TEAM1_KEY)),
          convertToSide(teams.get(TEAM2_KEY)),
          getGameTypeFromLiveGameKey(key))

      })

      Matches(games)
    })(onComplete)

  }

  /**
    * Return a list of the played matches.
    *
    * @return
    * a list of the played matches.
    */
  override def getSavedMatch(onSuccess: Seq[String] => Unit, onFail: Throwable => Unit): Unit = {
    Query(db =>
      db.keys(getGameHistoryPattern)
        .onComplete {
          case Success(keys) => onSuccess(keys.map(key => key.replace("game:", "").replace(":history", "")))
          case Failure(cause) => onFail(cause)
        }
    )
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
