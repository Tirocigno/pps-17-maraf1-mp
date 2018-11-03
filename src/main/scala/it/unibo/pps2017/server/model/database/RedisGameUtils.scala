package it.unibo.pps2017.server.model.database

import it.unibo.pps2017.server.model.GameType.GameType
import it.unibo.pps2017.server.model.{Game, LiveGame, Matches, SavedMatches, Side, StoredMatch}
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
  def getLiveMatches(onComplete: Matches => Unit): Unit


  /**
    * Search a list of played matches.
    *
    */
  def getSavedMatches(onSuccess: SavedMatches => Unit, onFail: Throwable => Unit): Unit
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
    val map: mutable.HashMap[String, String] = mutable.HashMap()

    //TODO sarebbe meglio avere un riferimento ai team in game
    map += TEAM1_KEY -> encodeSide(Side(Seq(game.players.head, game.players(2))))
    map += TEAM2_KEY -> encodeSide(Side(Seq(game.players(1), game.players(3))))
    map += SAVED_MATCH_GAME_KEY -> write(game)

    Query(db =>
      db.hmset(getGameHistoryKey(gameId), map.toMap)
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
      db.hget(getGameHistoryKey(gameId), SAVED_MATCH_GAME_KEY).map {
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

    map += TEAM1_KEY -> encodeSide(team1)
    map += TEAM2_KEY -> encodeSide(team2)

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
  override def getLiveMatches(onComplete: Matches => Unit): Unit = {
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
  override def getSavedMatches(onSuccess: SavedMatches => Unit, onFail: Throwable => Unit): Unit = {
    /*Query(db =>
      db.keys(getGameHistoryPattern)
        .onComplete {
          case Success(keys) => onSuccess(keys.map(key => key.replace("game:", "").replace(":history", "")))
          case Failure(cause) => onFail(cause)
        }
    )*/

    val games: ListBuffer[StoredMatch] = ListBuffer()

    BlockingQuery.withCallback(db => {
      db.keys(getGameHistoryPattern).forEach(key => {
        try {

          val hash = db.hgetAll(key)

          games += StoredMatch(key.replace("game:", "").replace(":history", ""),
            convertToSide(hash.get(TEAM1_KEY)),
            convertToSide(hash.get(TEAM2_KEY)))

        } catch {
          case ex: Exception => println("ECCEZIONE -> " + ex.getMessage)
        }

      })

      SavedMatches(games)
    })(onSuccess)
  }

  private def getGameIdFromLiveGameKey(key: String): String = {
    key.split(KEY_SPLITTER)(1).trim
  }

  private def getGameTypeFromLiveGameKey(key: String): String = {
    key.split(KEY_SPLITTER)(3).trim
  }

  private def encodeSide(side: Side): String = side.members.head + KEY_SPLITTER + side.members.last

  private def convertToSide(value: String): Side = {
    val members = value.split(KEY_SPLITTER)
    Side(Seq(members(0).trim, members(1).trim))
  }


}

object RedisGameUtils {

  def apply(): RedisGameUtils = new RedisGameUtils()
}
