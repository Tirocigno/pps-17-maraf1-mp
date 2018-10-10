
package it.unibo.pps2017.server.actor


import it.unibo.pps2017.core.game.{SimpleTeam, Team}
import it.unibo.pps2017.server.model.Lobby


sealed trait GameSearchMsg {
  val onGameFound: String => Unit
}

case class SinglePlayerMsg(id: String, onGameFound: String => Unit) extends GameSearchMsg

case class MultiPlayerMsg(id: String, partner: String, onGameFound: String => Unit) extends GameSearchMsg

case class SearchPlayerMsg(lobby: Option[Lobby], player: String, onGameFound: String => Unit) extends GameSearchMsg

case class SearchTeamMsg(lobby: Option[Lobby], team: SimpleTeam, onGameFound: String => Unit) extends GameSearchMsg

