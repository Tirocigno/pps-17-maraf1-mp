
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.NotifyGameIDMessage
import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.PartyRole.{Leader, Partner}
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference

/**
  * This trait handle the party component of every socialcontroller actor.
  */
trait SocialParty {

  /**
    * Check if the player is the leader of the party(then it can send/accept invite request) or not.
    *
    * @return true if the player is the leader of the party, false otherwise.
    */
  def isLeader: Boolean

  /**
    * Set a specific player with a specific role inside the party.
    *
    * @param role          the role on which the player will be set.
    * @param playerContext the player ID and actorRef.
    */
  def setPlayerInParty(role: PartyRole, playerContext: PlayerReference): Unit

  /**
    * Notify to all players inside the party that a game has started.
    *
    * @param gameID the id of the game
    */
  def notifyGameIDToAllPlayers(gameID: String): Unit

  /**
    * Reset the party, setting current player as leader of the party.
    */
  def resetParty(): Unit

  /**
    * Get all the players inside the party.
    *
    * @return a Map containing every player registered inside the party.
    */
  def getAllPlayers: Map[PartyRole, PlayerReference]

  /**
    * Remove the leadership from the current player and set a new role for him.
    *
    * @param partyRole the role current player will play as.
    */
  def markCurrentPlayerAs(partyRole: PartyRole): Unit

  /**
    * Get the reference of the player's partner, if present.
    *
    * @return a optional containing the partner references.
    */
  def getPartner: Option[PlayerReference]

}

object SocialParty {

  def apply(playerReference: PlayerReference): SocialParty = new SocialPartyImpl(playerReference)

  private class SocialPartyImpl(val currentReference: PlayerReference) extends SocialParty {
    var partyMap: Map[PartyRole, PlayerReference] = Map(Leader -> currentReference)

    override def isLeader: Boolean = partyMap.keys.exists(_.equals(Leader))

    override def resetParty(): Unit = partyMap = Map(Leader -> currentReference)

    override def getAllPlayers: Map[PartyRole, PlayerReference] = partyMap

    override def setPlayerInParty(role: PartyRole, playerContext: PlayerReference): Unit =
      partyMap += (role -> playerContext)

    override def markCurrentPlayerAs(partyRole: PartyRole): Unit = {
      partyMap -= Leader
      partyMap += (partyRole -> currentReference)
    }

    override def getPartner: Option[PlayerReference] = partyMap.get(Partner)

    override def notifyGameIDToAllPlayers(gameID: String): Unit =
      partyMap.values.filter(!_.equals(currentReference)).map(_.playerRef).foreach(_ ! NotifyGameIDMessage(gameID))
  }

}
