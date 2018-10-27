
package it.unibo.pps2017.commons.remote.social

import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference

/**
  * Trait to model all possible roles inside a party.
  */
sealed trait PartyRole

object PartyRole {

  case object Leader extends PartyRole

  case object Partner extends PartyRole

  case object Foe extends PartyRole

  case object FoePartner extends PartyRole

}

/**
  * A role containing the id of the player who plays as that role.
  */
sealed trait PartyPlayer

object PartyPlayer {

  case class PartnerPlayer(playerReference: PlayerReference) extends PartyPlayer

  case class FoePlayer(playerReference: PlayerReference) extends PartyPlayer

}
