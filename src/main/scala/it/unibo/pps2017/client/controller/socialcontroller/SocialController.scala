
package it.unibo.pps2017.client.controller.socialcontroller

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.controller.{ActorController, ClientController}
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.SocialActor
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.client.model.remote.{RestWebClient, SocialRestWebClient}
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.game.MatchNature.MatchNature
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, serializeActorRef}
import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, Partner}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{RegisterSocialIDAPI, UnregisterSocialIDAPI}
import it.unibo.pps2017.server.model.ServerApi.AddFriendAPI

/**
  * This trait is a mediator between the actor that handle the social
  * function of the system and the GUI.
  *
  */
trait SocialController extends ActorController {


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

  /**
    * Set the current GUI controller inside SocialActor
    *
    * @param gui the GUI to set.
    */
  def setCurrentGui(gui: SocialGUIController): Unit

}

object SocialController {

  val UNKNOWN_MESSAGE = "Unknown message received"

  private class SocialControllerImpl(val parentController: ClientController, val playerID: PlayerID, val
  discoveryContext: ServerContext) extends SocialController {

    val socialRestWebClient: RestWebClient = SocialRestWebClient(this, discoveryContext)
    override var currentActorRef: ActorRef = _
    var currentGUI: Option[SocialGUIController] = None


    override def setCurrentGui(gui: SocialGUIController): Unit = currentGUI = Some(gui)

    override def notifyCallResultToGUI(message: Option[String]): Unit = ???

    override def setOnlinePlayerList(onlinePlayers: SocialMap): Unit = {
      sendMessage(SetOnlinePlayersMapMessage(onlinePlayers))
    }

    /**
      * Notify an error to GUI.
      *
      * @param throwable the error occurred.
      */
    override def notifyErrorToGUI(throwable: Throwable): Unit =
      currentGUI.get.notifyErrorOccurred(throwable.getMessage)

    /**
      * Send a remote request to register a new friend.
      *
      * @param friendId the ID of the player to register.
      */
    override def registerNewFriend(friendId: PlayerID): Unit = {
      val paramMap = Map(AddFriendAPI.friendUsername -> friendId)
      socialRestWebClient.callRemoteAPI(AddFriendAPI, Some(paramMap))
    }

    /**
      * Notify the GUI updates inside the party.
      *
      * @param currentPartyMap the party current state.
      */
    override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit =
      currentGUI.get.updateParty(currentPartyMap.map(entry => (entry._1.asString, entry._2)))

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
    override def updateOnlinePlayerList(playerRefList: FriendList): Unit =
      currentGUI.get.updateOnlinePlayersList(playerRefList)

    /**
      * Update the current list of online friends displayed on GUI.
      *
      * @param friendList the new friendList to display.
      */
    override def updateOnlineFriendsList(friendList: FriendList): Unit = {
      currentGUI.get.updateOnlineFriendsList(friendList)
    }

    /**
      * Tell the actor to add a new friend.
      *
      * @param playerID the ID of the player to add as a friend.
      */
    override def tellFriendShipMessage(playerID: PlayerID): Unit = {
      sendMessage(TellAddFriendRequestMessage(playerID))
    }

    /**
      * Tell the actor to invite a player to play as his partner.
      *
      * @param playerID the ID of the player to invite.
      */
    override def tellInvitePlayerAsPartner(playerID: PlayerID): Unit = {
      sendMessage(TellInvitePlayerRequestMessage(playerID, Partner))
    }

    /**
      * Tell the actor to invite a player to play as his foe.
      *
      * @param playerID the ID of the player to invite.
      */
    override def tellInvitePlayerAsFoe(playerID: PlayerID): Unit = {
      sendMessage(TellInvitePlayerRequestMessage(playerID, Foe))
    }

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

    override def createActor(actorID: String, actorSystem: ActorSystem): Unit = {
      currentActorRef = SocialActor(actorSystem, this, actorID)
      registerToOnlinePlayerList()
    }

    private def registerToOnlinePlayerList(): Unit = {
      val encodedActorRef = serializeActorRef(currentActorRef)
      val paramMap = Map(RegisterSocialIDAPI.SOCIAL_ID -> playerID,
        RegisterSocialIDAPI.SOCIAL_ACTOR -> encodedActorRef)
      socialRestWebClient.callRemoteAPI(RegisterSocialIDAPI, Some(paramMap))
    }

    override def updateGUI(message: ActorMessage): Unit = message match {
      case response: AddFriendResponseMessage =>
        currentGUI.get.notifyMessageResponse(Some(response.senderID), response.socialResponse.message, response.request)
      case response: InvitePlayerResponseMessage =>
        currentGUI.get.notifyMessageResponse(response.myRole.map(_.playerReference.playerID),
          response.socialResponse.message, response.request)
      case AddFriendRequestMessage(sender) =>
        currentGUI.get.displayRequest(sender.playerID, None)
      case InvitePlayerRequestMessage(sender, role) =>
        currentGUI.get.displayRequest(sender.playerID, Some(role.asString))
      case _ => currentGUI.get.notifyErrorOccurred(UNKNOWN_MESSAGE)
    }

    private def unSubscribeFromOnlinePlayerList(): Unit = {
      val paramMap = Map(RegisterSocialIDAPI.SOCIAL_ID -> playerID)
      socialRestWebClient.callRemoteAPI(UnregisterSocialIDAPI, Some(paramMap))
    }

  }

}
