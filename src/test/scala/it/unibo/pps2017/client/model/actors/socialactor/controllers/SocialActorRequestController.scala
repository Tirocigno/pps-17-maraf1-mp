
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{AddFriendResponseMessage, TellAddFriendResponseMessage}
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID}

/**
  * Class to implement a mock controller which handle different request messages.
  */
abstract class SocialActorRequestController extends MockSocialController {

  override def updateGUI(message: ActorMessage): Unit

  def setCurrentActorRef(actorRef: ActorRef): Unit = currentActorRef = actorRef
}

object SocialActorRequestController {
  val MOCK_PLAYER_ID = "Carciofo"
}

class PositiveSocialActorAddFriendController extends SocialActorRequestController {
  override def updateGUI(message: ActorMessage): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(PositiveResponse, SocialActorRequestController.MOCK_PLAYER_ID)
  }
}

class NegativeSocialActorAddFriendController extends SocialActorRequestController {
  override def updateGUI(message: ActorMessage): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(NegativeResponse, SocialActorRequestController.MOCK_PLAYER_ID)
  }
}

class SenderSocialActorAddFriendController extends SocialActorRequestController {
  var playerID: PlayerID = _
  var response: SocialResponse = _

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = {}

  override def updateGUI(message: ActorMessage): Unit = message match {
    case AddFriendResponseMessage(response, sender) => playerID = sender; this.response = response
  }

  override def registerNewFriend(friendId: PlayerID): Unit = {}

}
