
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{AddFriendResponseMessage, TellAddFriendResponseMessage}
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID}

abstract class SocialActorAddFriendController extends MockSocialController {

  override def updateGUI(message: ActorMessage): Unit

  def setCurrentActorRef(actorRef: ActorRef): Unit = currentActorRef = actorRef
}

object SocialActorAddFriendController {
  val MOCK_PLAYER_ID = "Carciofo"
}

class PositiveSocialActorAddFriendController extends SocialActorAddFriendController {
  override def updateGUI(message: ActorMessage): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(PositiveResponse, SocialActorAddFriendController.MOCK_PLAYER_ID)
  }
}

class NegativeSocialActorAddFriendController extends SocialActorAddFriendController {
  override def updateGUI(message: ActorMessage): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(NegativeResponse, SocialActorAddFriendController.MOCK_PLAYER_ID)
  }
}

class SenderSocialActorAddFriendController extends SocialActorAddFriendController {
  var playerID: PlayerID = _
  var response: SocialResponse = _

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = {}

  override def updateGUI(message: ActorMessage): Unit = message match {
    case AddFriendResponseMessage(response, sender) => playerID = sender; this.response = response
  }

  override def registerNewFriend(friendId: PlayerID): Unit = {}

}
