package it.unibo.pps2017.server.model.database

import it.unibo.pps2017.server.model.Game
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.Serialization.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


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
}

object RedisGameUtils {

  def apply(): RedisGameUtils = new RedisGameUtils()
}
