
package it.unibo.pps2017.commons.remote.social

import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerID

/**
  * Trait to model all possible roles inside a party.
  */
sealed trait PartyRole

object PartyRole {

  case object Partner extends PartyRole

  case object Foe extends PartyRole

}

/**
  * A role containing the id of the player who plays as that role.
  */
sealed trait PartyPlayer extends PartyRole

object PartyPlayer {

  case class PartnerPlayer(playerID: PlayerID) extends PartyPlayer

  case class FoePlayer(playerID: PlayerID) extends PartyPlayer

}
