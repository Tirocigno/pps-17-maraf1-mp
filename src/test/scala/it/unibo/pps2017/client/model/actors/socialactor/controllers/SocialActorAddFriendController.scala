
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.TellAddFriendResponseMessage
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.FriendList

abstract class SocialActorAddFriendController extends MockSocialController {

  override def updateGUI(message: ActorMessage): Unit

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = {}

  def setCurrentActorRef(actorRef: ActorRef): Unit = currentActorRef = actorRef
}

object SocialActorAddFriendController {
  val MOCK_PLAYER_ID = "Carciofo"
}

class PositiveSocialActorAddFriendController extends SocialActorAddFriendController {
  override def updateGUI(message: ActorMessage): Unit = {
    println(currentActorRef)
    currentActorRef ! TellAddFriendResponseMessage(PositiveResponse, SocialActorAddFriendController.MOCK_PLAYER_ID)
  }
}

class NegativeSocialActorAddFriendController extends SocialActorAddFriendController {
  override def updateGUI(message: ActorMessage): Unit = {
    currentActorRef ! TellAddFriendResponseMessage(NegativeResponse, SocialActorAddFriendController.MOCK_PLAYER_ID)
  }

}
