
package it.unibo.pps2017.server.actor


import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.server.model.GameType.GameType
import it.unibo.pps2017.server.model.Lobby

import scala.collection.mutable.ListBuffer


sealed trait GameSearch {
  val onGameFound: String => Unit
}

case class TriggerSearch(team1: ListBuffer[String], team2: ListBuffer[String], onGameFound: String => Unit, gameType: GameType) extends GameSearch

case class SearchLobby(lobby: Option[Lobby], team1: SimpleTeam, team2: Option[SimpleTeam], onGameFound: String => Unit, gameType: GameType) extends GameSearch

