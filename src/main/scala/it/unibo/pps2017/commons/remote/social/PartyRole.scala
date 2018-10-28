
package it.unibo.pps2017.commons.remote.social

import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI

/**
  * Trait to model all possible roles inside a party.
  */
sealed trait PartyRole {
  def asRestParameter: String
}

object PartyRole {

  case object Leader extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.meParamKey
  }

  case object Partner extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.partnerParam
  }

  case object Foe extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.vsParam
  }

  case object FoePartner extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.vsPartnerParam
  }

}

/**
  * A role containing the id of the player who plays as that role.
  */
sealed trait PartyPlayer

object PartyPlayer {

  case class PartnerPlayer(playerReference: PlayerReference) extends PartyPlayer

  case class FoePlayer(playerReference: PlayerReference) extends PartyPlayer

}
