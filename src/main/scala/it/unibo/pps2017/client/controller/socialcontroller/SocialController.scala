
package it.unibo.pps2017.client.controller.socialcontroller

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.controller.ActorController
import it.unibo.pps2017.client.controller.clientcontroller.ClientController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.SocialActor
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.client.model.remote.{RestWebClient, SocialRestWebClient}
import it.unibo.pps2017.client.view.GuiStack
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.game.MatchNature.MatchNature
import it.unibo.pps2017.commons.remote.rest.RestUtils.ServerContext
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, Partner}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID}
import it.unibo.pps2017.commons.remote.social.{PartyRole, SocialResponse}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterSocialIDAPI
import it.unibo.pps2017.server.model.ServerApi.{AddFriendAPI, GetFriendsAPI, GetUserAPI}

import scala.collection.JavaConverters._

/**
  * This trait is a mediator between the actor that handle the social
  * function and the Social GUI.
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
    * Set the friend list inside the actor.
    *
    * @param friendList the list of friends of the current player.
    */
  def setFriendsList(friendList: FriendList): Unit

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
    * Notify the GUI the party was updated.
    *
    * @param currentPartyMap the party current state.
    */
  def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit

  /**
    * Execute a FoundGame call passing a paramMap containing all the party members, if present.
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
    * Tell the clientController to execute a FoundGame call.
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
    * Notify the controller user response to friend request,
    * the controller then will tell the response to his actor.
    *
    * @param socialResponse the response provided by GUI
    */
  def notifyFriendMessageResponse(socialResponse: SocialResponse): Unit

  /**
    * Notify the controller user response to invite request,
    * the controller then will tell the response to his actor.
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

  /**
    * Set the players score inside GUI.
    *
    * @param scores scores of the player.
    */
  def setScoreInsideGUI(scores: Int): Unit

  /**
    * Execute an api call with the effect to produce a heartbeat on the social topic channel.
    * This beat will be intercepted by the SocialActor which will obtain the reference to the RegistryActor.
    */
  def startHeartBeatRequest(): Unit

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

    override def startHeartBeatRequest(): Unit = registerToOnlinePlayerList()

    override def setCurrentGui(gui: SocialGUIController): Unit = {
      currentGUI = Some(gui)
      onGUISetting()
    }

    override def notifyCallResultToGUI(message: Option[String]): Unit =
      currentGUI.get.notifyAPIResult(message.get)

    override def setFriendsList(friendList: FriendList): Unit =
      sendMessage(SetFriendsList(friendList))


    override def notifyErrorToGUI(throwable: Throwable): Unit =
      currentGUI.get.notifyErrorOccurred(throwable.getMessage)

    override def registerNewFriend(friendId: PlayerID): Unit = {
      val paramMap = Map(AddFriendAPI.friendUsername -> friendId)
      socialRestWebClient.callRemoteAPI(AddFriendAPI, Some(paramMap), playerID)
    }

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
      currentGUI.get.resetGUI()
    }

    override def createActor(actorID: String, actorSystem: ActorSystem): Unit =
      currentActorRef = SocialActor(actorSystem, this, actorID)


    override def updateGUI(message: ActorMessage): Unit = message match {
      case response: AddFriendResponseMessage =>
        currentGUI.get.notifyMessageResponse(response.senderID, response.socialResponse.message, response.request)
      case response: InvitePlayerResponseMessage =>
        currentGUI.get.notifyMessageResponse(response.myRole.playerReference.playerID,
          response.socialResponse.message, response.request)
      case AddFriendRequestMessage(sender) =>
        currentGUI.get.displayRequest(sender.playerID, FRIEND_REQUEST)
      case InvitePlayerRequestMessage(sender, role) =>
        currentGUI.get.displayRequest(sender.playerID, role.asString)
      case _ => currentGUI.get.notifyErrorOccurred(UNKNOWN_MESSAGE)
    }

    override def notifyAllPlayersGameID(gameID: String): Unit =
      sendMessage(NotifyGameIDMessage(gameID))

    override def notifyGameController(gameID: String): Unit = {
      parentController.handleMatchResponse(gameID)
    }

    override def shutDown(): Unit = {
      currentActorRef ! KillYourSelf
    }

    override def notifyFriendMessageResponse(socialResponse: SocialResponse): Unit =
      currentActorRef ! TellAddFriendResponseMessage(socialResponse, playerID)

    override def notifyInviteMessageResponse(socialResponse: SocialResponse): Unit =
      currentActorRef ! TellInvitePlayerResponseMessage(socialResponse)

    override def getSocialGUIController: SocialGUIController = currentGUI.get

    override def setScoreInsideGUI(scores: Int): Unit = currentGUI.get.setTotalPoints(scores)

    /**
      * Start a call to generate an heartbeat from the registry actor located on discovery.
      */
    private def registerToOnlinePlayerList(): Unit = {
      socialRestWebClient.callRemoteAPI(RegisterSocialIDAPI, None)
      fetchFriendList()
    }

    /**
      * Fetch from server the friend list with an API call.
      */
    private def fetchFriendList(): Unit =
      socialRestWebClient.callRemoteAPI(GetFriendsAPI, None, playerID)


    /**
      * Set new operations on GUI closing.
      */
    private def onGUISetting(): Unit = {
      socialRestWebClient.callRemoteAPI(GetUserAPI, None, playerID)
      GuiStack().stage.setOnCloseRequest(_ => {
        this.shutDown()
        System.exit(0)
      })
    }

  }

}
