
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference

/**
  * This trait handle the party component of every social actor.
  */
trait SocialParty {

  def isLeader: Boolean

  def setPlayerInParty(role: PartyRole, playerContext: PlayerReference)

  def notifyGameIDToAllPlayers(gameID: String)

  def resetParty()

  def getAllPlayers: Map[PartyRole, PlayerReference]

}
