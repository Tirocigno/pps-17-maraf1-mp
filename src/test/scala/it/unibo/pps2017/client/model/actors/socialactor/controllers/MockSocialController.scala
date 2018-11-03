
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.game.MatchNature
import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}

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

  /**
    * Tell the actor to add a new friend.
    *
    * @param playerID the ID of the player to add as a friend.
    */
  override def tellFriendShipMessage(playerID: PlayerID): Unit = {}

  /**
    * Tell the actor to invite a player to play as his partner.
    *
    * @param playerID the ID of the player to invite.
    */
  override def tellInvitePlayerAsPartner(playerID: PlayerID): Unit = {}

  /**
    * Tell the actor to invite a player to play as his foe.
    *
    * @param playerID the ID of the player to invite.
    */
  override def tellInvitePlayerAsFoe(playerID: PlayerID): Unit = {}

  /**
    * Start a new game
    *
    * @param matchNature the nature of the game to play.
    */
  override def startGame(matchNature: MatchNature.MatchNature): Unit = {}

  /**
    * Reset the party and notify the GUI a match conclusion.
    */
  override def finishGame(): Unit = {}

  /**
    * Set the current GUI controller inside SocialActor
    *
    * @param gui the GUI to set.
    */
  override def setCurrentGui(gui: SocialGUIController): Unit = {}

  /**
    * Notify all the players that a gameID has arrived.
    *
    * @param gameID id of the game notified to party.
    */
  override def notifyAllPlayersGameID(gameID: String): Unit = {}

  /**
    * Notify game controller that a game has been joined.
    *
    * @param gameID the joined game's id.
    */
  override def notifyGameController(gameID: String): Unit = {}

  /**
    * Shutdown the socialActor and remove its reference from the online list.
    */
  override def shutDown(): Unit = {}
}
