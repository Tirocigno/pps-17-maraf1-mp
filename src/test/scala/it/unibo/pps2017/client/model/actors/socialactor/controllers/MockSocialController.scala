
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.game.MatchNature
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}
import it.unibo.pps2017.commons.remote.social.{PartyRole, SocialResponse}

/**
  * Mock implementation of a SocialGUIController.
  */
class MockSocialController extends SocialController {

  override var currentActorRef: ActorRef = _

  override def notifyCallResultToGUI(message: Option[String]): Unit = {}

  override def setOnlinePlayerList(playerList: SocialMap): Unit = {}

  override def notifyErrorToGUI(throwable: Throwable): Unit = {}

  override def registerNewFriend(friendId: PlayerID): Unit = {}

  override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit = {}

  override def executeFoundGameCall(paramMap: Map[String, String]): Unit = {}

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = {}

  override def updateOnlineFriendsList(friendList: FriendList): Unit = {}

  override def createActor(actorID: String, actorSystem: ActorSystem): Unit = {}

  override def updateGUI(message: ActorMessage): Unit = {}

  override def tellFriendShipMessage(playerID: PlayerID): Unit = {}

  override def tellInvitePlayerAsPartner(playerID: PlayerID): Unit = {}

  override def tellInvitePlayerAsFoe(playerID: PlayerID): Unit = {}

  override def startGame(matchNature: MatchNature.MatchNature): Unit = {}

  override def finishGame(): Unit = {}

  override def setCurrentGui(gui: SocialGUIController): Unit = {}

  override def notifyAllPlayersGameID(gameID: String): Unit = {}

  override def notifyGameController(gameID: String): Unit = {}

  override def shutDown(): Unit = {}

  override def notifyFriendMessageResponse(socialResponse: SocialResponse): Unit = {}

  override def notifyInviteMessageResponse(socialResponse: SocialResponse): Unit = {}

  override def getSocialGUIController: SocialGUIController = new it.unibo.pps2017.core.gui.SocialGUIController()

  override def setScoreInsideGUI(scores: Int): Unit = {}
}
