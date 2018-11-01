package it.unibo.pps2017.server.model

import it.unibo.pps2017.server.model.GameType.GameType
import org.json4s.DefaultFormats

package object database {

  implicit val formats: DefaultFormats.type = DefaultFormats

  val USER_SCORE_KEY: String = "score"
  val STARTING_SCORE: Int = 200
  val SCORE_INC: Int = 10
  val SCORE_DECR: Int = -10
  val RANKING_KEY: String = "ranking"

  val GAME_TIME_TO_LIVE: Long = 7200
  val KEY_SPLITTER: String = ":"

  val TEAM1_KEY: String = "team1"
  val TEAM2_KEY: String = "team2"

  def getUserKey(username: String): String = "user:" + username

  def getUserFriendsKey(username: String): String = "user:" + username + ":friends"

  def getGameHistoryKey(gameId: String): String = "game:" + gameId + ":history"

  def getInGameKey(gameId: String, gameType: GameType): String = "game:" + gameId + ":ingame:" + gameType.asString

  def getLiveKeyPattern: String = "game:*:ingame:*"
}


