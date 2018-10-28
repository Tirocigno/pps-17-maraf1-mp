package it.unibo.pps2017.commons.remote.social

/**
  * Trait to model the possible response for a message.
  */
sealed trait SocialResponse {
  def message: String
}

object SocialResponse {

  case object PositiveResponse extends SocialResponse {
    override def message = " accepted your request"
  }

  case object NegativeResponse extends SocialResponse {
    override def message = " refused your request"
  }

}


