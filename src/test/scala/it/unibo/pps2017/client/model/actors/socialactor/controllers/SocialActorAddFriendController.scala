
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.TellAddFriendResponseMessage
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}

abstract class SocialActorAddFriendController extends MockSocialController {

  val MOCK_PLAYER_ID = "Carciofo"

  override def notifyCallResultToGUI(message: String): Unit

  def setCurrentActorRef(actorRef: ActorRef): Unit = currentActorRef = actorRef
}

class PositiveSocialActorAddFriendController extends SocialActorAddFriendController {
  override def notifyCallResultToGUI(message: String): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(PositiveResponse, MOCK_PLAYER_ID)
  }
}

class NegativeSocialActorAddFriendController extends SocialActorAddFriendController {
  override def notifyCallResultToGUI(message: String): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(NegativeResponse, MOCK_PLAYER_ID)
  }
}
