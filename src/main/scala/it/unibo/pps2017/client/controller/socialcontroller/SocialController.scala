
package it.unibo.pps2017.client.controller.socialcontroller

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.controller.{ActorController, ClientController}
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.game.MatchNature.MatchNature
import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}

/**
  * This trait is a mediator between the actor that handle the social
  * function of the system and the GUI.
  *
  */
trait SocialController extends ActorController {

  override var currentGUI: SocialGUIController = _

  override def setGUI[A <: SocialGUIController](gui: A): Unit = this.currentGUI = gui

  /**
    * Notify a API callback response to the GUI
    *
    * @param message the body of the response.
    */
  def notifyCallResultToGUI(message: Option[String]): Unit

  /**
    * Set the current online players map.
    *
    * @param onlinePlayers the list of current online players.
    */
  def setOnlinePlayerList(onlinePlayers: SocialMap): Unit

  /**
    * Notify an error to GUI
    *
    * @param throwable the error occurred.
    */
  def notifyErrorToGUI(throwable: Throwable): Unit

  /**
    * Send a remote request to register a new friend.
    *
    * @param friendId the ID of the player to register.
    */
  def registerNewFriend(friendId: PlayerID): Unit

  /**
    * Notify the GUI updates inside the party.
    *
    * @param currentPartyMap the party current state.
    */
  def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit

  /**
    * Execute a FoundGame call passing a paramMap.
    *
    * @param paramMap the parameters of the call to execute.
    */
  def executeFoundGameCall(paramMap: Map[String, String]): Unit

  /**
    * Update the current list of online players displayed on GUI.
    *
    * @param playerRefList the new onlinePlayer list to display.
    */
  def updateOnlinePlayerList(playerRefList: FriendList): Unit

  /**
    * Update the current list of online friends displayed on GUI.
    *
    * @param friendList the new friendList to display.
    */
  def updateOnlineFriendsList(friendList: FriendList): Unit

  /**
    * Tell the actor to add a new friend.
    *
    * @param playerID the ID of the player to add as a friend.
    */
  def tellFriendShipMessage(playerID: PlayerID): Unit

  /**
    * Tell the actor to invite a player to play as his partner.
    *
    * @param playerID the ID of the player to invite.
    */
  def tellInvitePlayerAsPartner(playerID: PlayerID): Unit

  /**
    * Tell the actor to invite a player to play as his foe.
    *
    * @param playerID the ID of the player to invite.
    */
  def tellInvitePlayerAsFoe(playerID: PlayerID): Unit

  /**
    * Start a new game
    *
    * @param matchNature the nature of the game to play.
    */
  def startGame(matchNature: MatchNature): Unit

  /**
    * Reset the party and notify the GUI a match conclusion.
    */
  def finishGame(): Unit

}

object SocialController {

  private class SocialControllerImpl(val parentController: ClientController) extends SocialController {

    override var currentActorRef: ActorRef = _

    override def notifyCallResultToGUI(message: Option[String]): Unit = ???

    /**
      * Set the current online players map.
      *
      * @param onlinePlayers the list of current online players.
      */
    override def setOnlinePlayerList(onlinePlayers: SocialMap): Unit = ???

    /**
      * Notify an error to GUI
      *
      * @param throwable the error occurred.
      */
    override def notifyErrorToGUI(throwable: Throwable): Unit = ???

    /**
      * Send a remote request to register a new friend.
      *
      * @param friendId the ID of the player to register.
      */
    override def registerNewFriend(friendId: PlayerID): Unit = ???

    /**
      * Notify the GUI updates inside the party.
      *
      * @param currentPartyMap the party current state.
      */
    override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit = ???

    /**
      * Execute a FoundGame call passing a paramMap.
      *
      * @param paramMap the parameters of the call to execute.
      */
    override def executeFoundGameCall(paramMap: Map[String, String]): Unit = ???

    /**
      * Update the current list of online players displayed on GUI.
      *
      * @param playerRefList the new onlinePlayer list to display.
      */
    override def updateOnlinePlayerList(playerRefList: FriendList): Unit = ???

    /**
      * Update the current list of online friends displayed on GUI.
      *
      * @param friendList the new friendList to display.
      */
    override def updateOnlineFriendsList(friendList: FriendList): Unit = ???

    /**
      * Tell the actor to add a new friend.
      *
      * @param playerID the ID of the player to add as a friend.
      */
    override def tellFriendShipMessage(playerID: PlayerID): Unit = ???

    /**
      * Tell the actor to invite a player to play as his partner.
      *
      * @param playerID the ID of the player to invite.
      */
    override def tellInvitePlayerAsPartner(playerID: PlayerID): Unit = ???

    /**
      * Tell the actor to invite a player to play as his foe.
      *
      * @param playerID the ID of the player to invite.
      */
    override def tellInvitePlayerAsFoe(playerID: PlayerID): Unit = ???

    /**
      * Start a new game
      *
      * @param matchNature the nature of the game to play.
      */
    override def startGame(matchNature: MatchNature): Unit = ???

    /**
      * Reset the party and notify the GUI a match conclusion.
      */
    override def finishGame(): Unit = ???

    override def createActor(actorID: String, actorSystem: ActorSystem): Unit = ???

    override def updateGUI(message: ActorMessage): Unit = ???
  }

}
