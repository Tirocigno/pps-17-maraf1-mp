package it.unibo.pps2017.client.model.actors.socialactor.socialmessages

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.SocialUtils.SocialMap


/**
  * This object will contains all the possible messages sent or received by the socialActor
  */
object SocialMessages {

  sealed trait SocialMessage extends ActorMessage

  case class SetOnlinePlayersMap(socialMap: SocialMap)

}
