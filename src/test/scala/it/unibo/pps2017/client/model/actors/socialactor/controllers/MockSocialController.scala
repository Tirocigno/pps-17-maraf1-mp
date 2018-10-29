
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}

/**
  * Mock implementation of a SocialController.
  */
class MockSocialController extends SocialController {

  override var currentActorRef: ActorRef = _

  override def notifyCallResultToGUI(message: Option[String]): Unit = ???

  override def setOnlinePlayerList(playerList: SocialMap): Unit = ???

  override def notifyErrorToGUI(throwable: Throwable): Unit = ???

  override def registerNewFriend(friendId: PlayerID): Unit = ???

  override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit = ???

  override def executeFoundGameCall(paramMap: Map[String, String]): Unit = ???

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = ???

  override def updateOnlineFriendsList(friendList: FriendList): Unit = ???

  override def createActor(actorID: String, actorSystem: ActorSystem): Unit = ???

  override def updateGUI(message: ActorMessage): Unit = ???

}
