package it.unibo.pps2017.commons.remote.social

/**
  * Trait to model the possible response for a message.
  */
sealed trait SocialResponse

object SocialResponse {

  case object PositiveResponse extends SocialResponse

  case object NegativeResponse extends SocialResponse

}


