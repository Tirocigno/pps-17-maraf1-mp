
package it.unibo.pps2017.commons.remote.social

import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI

/**
  * Trait to model all possible roles inside a party.
  */
sealed trait PartyRole {
  def asRestParameter: String

  def asString: String
}

object PartyRole {

  /**
    * Leader role.
    */
  case object Leader extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.meParamKey

    override def asString: String = "Leader"
  }

  /**
    * Partner Role
    */
  case object Partner extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.partnerParam

    override def asString: String = "Partner"
  }

  /**
    * Foe role.
    */
  case object Foe extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.vsParam

    override def asString: String = "Foe"
  }

  /**
    * Foe partner role.
    */
  case object FoePartner extends PartyRole {
    override def asRestParameter: String = FoundGameRestAPI.vsPartnerParam

    override def asString: String = "FoePartner"
  }

}

/**
  * A trait containing a player reference and the role as which he will fit inside the party.
  */
sealed trait PartyPlayer {
  def playerReference: PlayerReference
}

object PartyPlayer {

  /**
    * Partner player.
    *
    * @param playerReference the playerReference of that player.
    */
  case class PartnerPlayer(playerReference: PlayerReference) extends PartyPlayer

  /**
    * Foe player.
    *
    * @param playerReference the playerReference of that player.
    */
  case class FoePlayer(playerReference: PlayerReference) extends PartyPlayer

}
