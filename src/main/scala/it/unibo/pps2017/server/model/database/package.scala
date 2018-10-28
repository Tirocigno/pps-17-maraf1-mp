package it.unibo.pps2017.server.model

import org.json4s.DefaultFormats

package object database {

  implicit val formats: DefaultFormats.type = DefaultFormats

  def getUserKey(username: String): String = "user:" + username

  def getUserFriendsKey(username: String): String = "user:" + username + ":friends"

  def getGameHistoryKey(gameId: String): String = "game:" + gameId + ":history"
}


