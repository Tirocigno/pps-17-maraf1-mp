
package it.unibo.pps2017.client.controller.socialcontroller

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import it.unibo.pps2017.client.controller.ActorController
import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.SocialActor
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.client.model.remote.{RestWebClient, SocialRestWebClient}
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.game.MatchNature.MatchNature
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, serializeActorRef}
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, Partner}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}
import it.unibo.pps2017.commons.remote.social.{PartyRole, SocialResponse}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{RegisterSocialIDAPI, UnregisterSocialIDAPI}
import it.unibo.pps2017.server.model.ServerApi.AddFriendAPI

import scala.collection.JavaConverters._

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
    * Notify all the players that a gameID has arrived.
    *
    * @param gameID id of the game notified to party.
    */
  def notifyAllPlayersGameID(gameID: String): Unit

  /**
    * Notify game controller that a game has been joined.
    *
    * @param gameID the joined game's id.
    */
  def notifyGameController(gameID: String): Unit

  /**
    * Set the current GUI controller inside SocialActor
    *
    * @param gui the GUI to set.
    */
  def setCurrentGui(gui: SocialGUIController): Unit

  /**
    * Shutdown the socialActor and remove its reference from the online list.
    */
  def shutDown(): Unit

  /**
    * Notify the controller user response to friend request.
    *
    * @param socialResponse the response provided by GUI
    */
  def notifyFriendMessageResponse(socialResponse: SocialResponse): Unit

  /**
    * Notify the controller user response to invite request.
    *
    * @param socialResponse the response provided by GUI
    */
  def notifyInviteMessageResponse(socialResponse: SocialResponse): Unit

  /**
    * Getter for SocialGUIController.
    *
    * @return the social GUI controller.
    */
  def getSocialGUIController: SocialGUIController

}

object SocialController {

  val UNKNOWN_MESSAGE = "Unknown message received"
  val FRIEND_REQUEST = "Friend request"

  def apply(parentController: ClientController,
            playerID: PlayerID, discoveryContext: ServerContext): SocialController =
    new SocialControllerImpl(parentController, playerID, discoveryContext)

  private class SocialControllerImpl(val parentController: ClientController, val playerID: PlayerID, val
  discoveryContext: ServerContext) extends SocialController {

    val socialRestWebClient: RestWebClient = SocialRestWebClient(this, discoveryContext)
    override var currentActorRef: ActorRef = _
    var currentGUI: Option[SocialGUIController] = None
    var matchNature: Option[MatchNature] = None


    override def setCurrentGui(gui: SocialGUIController): Unit = currentGUI = Some(gui)

    override def notifyCallResultToGUI(message: Option[String]): Unit =
      currentGUI.get.notifyAPIResult(message.get)

    override def setOnlinePlayerList(onlinePlayers: SocialMap): Unit = {
      sendMessage(SetOnlinePlayersMapMessage(onlinePlayers))
    }

    override def notifyErrorToGUI(throwable: Throwable): Unit =
      currentGUI.get.notifyErrorOccurred(throwable.getMessage)

    override def registerNewFriend(friendId: PlayerID): Unit =
      socialRestWebClient.callRemoteAPI(AddFriendAPI, None, friendId)


    override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit =
      currentGUI.get.updateParty(currentPartyMap.map(entry => (entry._1.asString, entry._2)).asJava)

    override def executeFoundGameCall(paramMap: Map[String, String]): Unit =
      parentController.sendMatchRequest(matchNature.get, Some(paramMap))

    override def updateOnlinePlayerList(playerRefList: FriendList): Unit =
      currentGUI.get.updateOnlinePlayersList(playerRefList.asJava)

    override def updateOnlineFriendsList(friendList: FriendList): Unit = {
      currentGUI.get.updateOnlineFriendsList(friendList.asJava)
    }

    override def tellFriendShipMessage(playerID: PlayerID): Unit = {
      sendMessage(TellAddFriendRequestMessage(playerID))
    }

    override def tellInvitePlayerAsPartner(playerID: PlayerID): Unit = {
      sendMessage(TellInvitePlayerRequestMessage(playerID, Partner))
    }

    override def tellInvitePlayerAsFoe(playerID: PlayerID): Unit = {
      sendMessage(TellInvitePlayerRequestMessage(playerID, Foe))
    }

    override def startGame(matchNature: MatchNature): Unit = {
      this.matchNature = Some(matchNature)
      sendMessage(GetPartyAndStartGameMessage)
    }


    override def finishGame(): Unit = {
      sendMessage(ResetParty)
      currentGUI.get.updateParty(Map[String,String]().asJava)
    }

    override def createActor(actorID: String, actorSystem: ActorSystem): Unit = {
      currentActorRef = SocialActor(actorSystem, this, actorID)
      registerToOnlinePlayerList()
    }

    override def updateGUI(message: ActorMessage): Unit = message match {
      case response: AddFriendResponseMessage =>
        currentGUI.get.notifyMessageResponse(response.senderID, response.socialResponse.message, response.request)
      case response: InvitePlayerResponseMessage =>
        currentGUI.get.notifyMessageResponse(response.myRole.map(_.playerReference.playerID).get,
          response.socialResponse.message, response.request)
      case AddFriendRequestMessage(sender) =>
        currentGUI.get.displayRequest(sender.playerID, FRIEND_REQUEST)
      case InvitePlayerRequestMessage(sender, role) =>
        currentGUI.get.displayRequest(sender.playerID, role.asString)
      case _ => currentGUI.get.notifyErrorOccurred(UNKNOWN_MESSAGE)
    }

    override def notifyAllPlayersGameID(gameID: String): Unit =
      sendMessage(NotifyGameIDMessage(gameID))

    override def notifyGameController(gameID: String): Unit = parentController.handleMatchResponse(gameID)

    override def shutDown(): Unit = {
      unSubscribeFromOnlinePlayerList()
      currentActorRef ! PoisonPill
    }

    override def notifyFriendMessageResponse(socialResponse: SocialResponse): Unit =
      currentActorRef ! TellAddFriendResponseMessage(socialResponse, playerID)

    override def notifyInviteMessageResponse(socialResponse: SocialResponse): Unit =
      currentActorRef ! TellInvitePlayerResponseMessage(socialResponse)

    override def getSocialGUIController: SocialGUIController = currentGUI.get

    private def registerToOnlinePlayerList(): Unit = {
      val encodedActorRef = serializeActorRef(currentActorRef)
      val paramMap = Map(RegisterSocialIDAPI.SOCIAL_ID -> playerID,
        RegisterSocialIDAPI.SOCIAL_ACTOR -> encodedActorRef)
      socialRestWebClient.callRemoteAPI(RegisterSocialIDAPI, Some(paramMap))
    }

    private def unSubscribeFromOnlinePlayerList(): Unit = {
      val paramMap = Map(RegisterSocialIDAPI.SOCIAL_ID -> playerID)
      socialRestWebClient.callRemoteAPI(UnregisterSocialIDAPI, Some(paramMap))
    }

  }

}
