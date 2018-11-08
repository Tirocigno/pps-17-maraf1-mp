package it.unibo.pps2017.server.model

import it.unibo.pps2017.server.model.GameType.GameType
import org.json4s.DefaultFormats
import redis.RedisClient
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

package object database {

  implicit val formats: DefaultFormats.type = DefaultFormats

  val TEAM1_KEY: String = "team1"
  val TEAM2_KEY: String = "team2"
  val RANKING_KEY: String = "ranking"
  val USER_SCORE_KEY: String = "score"
  val SAVED_MATCH_GAME_KEY: String = "game"

  val STARTING_SCORE: Int = 200
  val SCORE_INC: Int = 10
  val SCORE_DECR: Int = -10


  val GAME_TIME_TO_LIVE: Long = 7200
  val KEY_SPLITTER: String = ":"


  def getUserKey(username: String): String = "user:" + username

  def getUserFriendsKey(username: String): String = "user:" + username + ":friends"

  def getGameHistoryKey(gameId: String): String = "game:" + gameId + ":history"

  def getGameHistoryPattern: String = "game:*:history"

  def getInGameKey(gameId: String, gameType: GameType): String = "game:" + gameId + ":ingame:" + gameType.asString

  def getLiveKeyPattern: String = "game:*:ingame:*"



  implicit class RedisClientCloser(db: RedisClient) {
    def closeConnection(): Unit = db.quit().onComplete {
      case Success(res) => if (res) {
        println("Redis connection closed successfully!")
        db.stop()
      } else {
        println("Error on Redis connection closing!")
      }
      case Failure(cause) => println(s"Unexpected error on Redis connection closing. \nDetails: ${cause.getMessage}!")
    }
  }

  implicit class JedisCloser(db: Jedis) {
    def closeConnection(): Unit = try{
      println(s"Redis blocking client closing connection result: ${db.quit()}")
      db.close()
    } catch {
      case cause: Exception => println(s"Error on closing a Redis blocking connection. \n Details: ${cause.getMessage}")
    }
  }

}


