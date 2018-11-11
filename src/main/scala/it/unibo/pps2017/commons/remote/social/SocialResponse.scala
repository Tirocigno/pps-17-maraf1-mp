package it.unibo.pps2017.commons.remote.social

/**
  * Trait to model the possible response for a message.
  */
sealed trait SocialResponse {
  def message: String
}

object SocialResponse {

  case object PositiveResponse extends SocialResponse {
    override def message = "positive"
  }

  case object NegativeResponse extends SocialResponse {
    override def message = "negative"
  }

}


