
package it.unibo.pps2017.core

import akka.actor.ActorRef
import it.unibo.pps2017.core.player.{Player, PlayerImpl}

package object game {
  /**
    * Basic ID for teams.
    */
  val firstTeamID = "Team1"
  val secondTeamID = "Team2"

  /**
    * Generate a player with empty ActorRef for test purpose.
    * @param playerName the player's name.
    * @return a Player object with empty actorRef.
    */
  def generateTestPlayer(playerName:String):Player = PlayerImpl(playerName,ActorRef.noSender)
}
