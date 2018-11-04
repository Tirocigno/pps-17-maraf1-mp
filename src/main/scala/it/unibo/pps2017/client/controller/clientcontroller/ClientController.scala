
package it.unibo.pps2017.client.controller.clientcontroller

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.controller.{Controller, getRandomID}
import it.unibo.pps2017.client.model.remote.{GameRestWebClient, RestWebClient}
import it.unibo.pps2017.client.view._
import it.unibo.pps2017.client.view.login.LoginGUIController
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.commons.remote.game.MatchNature
import it.unibo.pps2017.commons.remote.game.MatchNature.MatchNature
import it.unibo.pps2017.commons.remote.rest.API.RestAPI
import it.unibo.pps2017.commons.remote.rest.RestUtils.{IPAddress, MatchRef, Port, ServerContext}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetAllMatchesAPI
import it.unibo.pps2017.server.model.Game
import it.unibo.pps2017.server.model.ServerApi._

import scala.collection.JavaConverters._


sealed trait ClientController extends Controller {

  /**
    * Notify an error occurred inside the model to GUI.
    *
    * @param throwable the error occurred.
    */
  def notifyError(throwable: Throwable)

  /**
    * Set the GUI inside the ClientController.
    *
    * @param gui a GenericGUIController to be set inside the clientcontroller.
    */
  def setCurrentGUI(gui: GUIController): Unit


  /**
    * Start an actorsystem which will join the seed disposed by the discovery.
    *
    * @param seedHost IP address of the seeds.
    * @param myIP     IP address of the machine on which actor system is running.
    */
  def startActorSystem(seedHost: IPAddress, myIP: IPAddress)


  /** Create a rest client configured with a discovery server address and port.
    *
    * @param discoveryIP   the IP on which discoveryServer is running.
    * @param discoveryPort the port on which discoveryServer is running.
    */
  def createRestClient(discoveryIP: String, discoveryPort: Port)

  /**
    * Send a match request to the server.
    *
    * @param matchNature the nature of a match, can be competitive or not.
    * @param paramMap a map containing party parameters, if present.
    */
  def sendMatchRequest(matchNature: MatchNature, paramMap: Option[Map[String, String]]): Unit

  /**
    * Start the game GUI and notify GameActor and SocialActor the player has joined a match.
    *
    * @param gameID a string containing the gameID, notify by the server.
    */
  def handleMatchResponse(gameID: String): Unit

  /**
    * Send a login request to a server.
    *
    * @param userName a string containing the username of the player.
    * @param password a string containing the password of the player.
    */
  def sendLoginRequest(userName: String, password: String): Unit

  /**
    * Handle a login response.
    *
    * @param response the response given to the login or authentication request.
    */
  def handleLoginAndRegistrationResponse(response: String): Unit

  /**
    * Send a registration request to remote server.
    *
    * @param userName a string containing the username of the player to register.
    * @param password a string containing the password of the player to register.
    */
  def sendRegisterRequest(userName: String, password: String): Unit

  /**
    * Fetch current played matches from server.
    */
  def fetchCurrentMatchesList(): Unit

  /**
    * Display current played matches.
    *
    * @param playedMatches a list containing all matchIDs as strings.
    */
  def displayCurrentMatchesList(playedMatches: List[MatchRef]): Unit

  /**
    * Fetch the archive of matches played from a server.
    */
  def fetchRegisteredMatchesList(): Unit

  /**
    * Display the archive of matches.
    *
    * @param playedMatches a list containing all matchIDs as String
    */
  def displayRegisteredMatchesList(playedMatches: List[MatchRef]): Unit

  /**
    * Start watching a current played match.
    *
    * @param matchID ID of the match to watch.
    */
  def startMatchWatching(matchID: String): Unit

  /**
    * Start replay a played match.
    *
    * @param matchID ID of the match to replay.
    */
  def startMatchReplay(matchID: String): Unit

  /**
    * Execute a match replay based on the game taken as input.
    *
    * @param gameToReplay the game to be replayed.
    */
  def handleMatchReplay(gameToReplay: Game): Unit

  /**
    * Start the generic gui when invoked.
    */
  def startGenericGUI(): Unit

  /**
    * Notify to whole system that a game is finished.
    */
  def notifyGameFinished():Unit
}

object ClientController {

  private val staticController = new ClientControllerImpl

  /**
    * Apply is not used in order to mantain the singleton reference outside the object implementation.
    *
    * @return the singleton clientController
    */
  def getSingletonController: ClientController = staticController

  private class ClientControllerImpl() extends ClientController {
    val gameController = new GameController()
    val guiStack: GuiStack = GuiStack()
    var playerName: String = getRandomID
    var unconfirmedUserName: String = playerName
    var actorSystem: Option[ActorSystem] = None
    var webClient: Option[RestWebClient] = None
    var socialController: Option[SocialController] = None
    var genericGui: Option[GenericGUIController] = None
    var loginGUI: Option[LoginGUIController] = None

    override def notifyError(throwable: Throwable): Unit = {
      if (genericGui.isDefined) genericGui.get.notifyError(throwable)
      if (loginGUI.isDefined) loginGUI.get.notifyError(throwable)
      if (socialController.isDefined) socialController.get.getSocialGUIController.notifyError(throwable)
    }


    override def startActorSystem(seedHost: IPAddress, myIP: IPAddress): Unit = {
      actorSystem = Some(AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(seedHost, "0", myIP))
    }


    override def createRestClient(discoveryIP: String, discoveryPort: Port): Unit = {
      webClient = Some(GameRestWebClient(ServerContext(discoveryIP, discoveryPort)))
    }

    override def sendMatchRequest(matchNature: MatchNature,
                                  paramMap: Option[Map[String, String]]): Unit = paramMap match {
      case Some(_) => startMatch(paramMap.get, matchNature)
      case None => val map = Map(FoundGameRestAPI.meParamKey -> playerName)
        startMatch(map, matchNature)
    }


    override def setCurrentGUI(gui: GUIController): Unit = gui match {
      case genericController: GenericGUIController => genericGui = Some(genericController)
      case loginGUIController: LoginGUIController => loginGUI = Some(loginGUIController)
    }


    override def handleMatchResponse(gameID: String): Unit = {
      gameController.createActor(this.playerName, actorSystem.get)

      guiStack.setCurrentScene(GameStage, gameController)
      gameController.joinPlayerToMatch(gameID)

      socialController match {
        case Some(controller) => controller.notifyAllPlayersGameID(gameID)
        case None =>
      }
    }

    override def sendLoginRequest(userName: String, password: String): Unit =
      launchAutentichationAPI(LoginAPI, userName, password)

    private def launchAutentichationAPI(api: RestAPI, username: String, password: String): Unit = {
      val map = Map(LoginAPI.password -> password)
      webClient.get.callRemoteAPI(api, Some(map), username)
    }

    override def handleLoginAndRegistrationResponse(message: String): Unit = {
      loginGUI.get.handleResponse(message)
      //onAuthenticationSucceded()
    }

    override def fetchCurrentMatchesList(): Unit =
      webClient.get.callRemoteAPI(GetAllMatchesAPI, None)

    private def startMatch(paramMap: Map[String, String], matchNature: MatchNature): Unit = matchNature match {
      case MatchNature.CasualMatch => webClient.get.callRemoteAPI(FoundGameRestAPI, Some(paramMap))
      case MatchNature.CompetitiveMatch => val map = paramMap +
        (FoundGameRestAPI.RANKED_PARAMETER -> FoundGameRestAPI.RANKED_VALUE)
        webClient.get.callRemoteAPI(FoundGameRestAPI, Some(map))
    }

    override def sendRegisterRequest(userName: String, password: String): Unit =
      launchAutentichationAPI(AddUserAPI, userName, password)

    /**
      * Define the operation to do after a successful authentication.
      */
    private def onAuthenticationSucceded(): Unit = {
      playerName = unconfirmedUserName
      socialController =
        Some(SocialController(this, playerName, webClient.get.discoveryServerContext))
      socialController.get.createActor(playerName, actorSystem.get)
      guiStack.setCurrentScene(SocialStage, socialController.get)
    }


    override def startMatchWatching(matchID: String): Unit = {
      println("Entro in matchwatching")
      guiStack.setCurrentScene(GameStage, gameController)

      gameController.createViewerActor(this.playerName, actorSystem.get)
      gameController.joinPlayerToMatch(matchID)
    }

    /**
      * Fetch the archive of matches played from a server.
      */
    override def fetchRegisteredMatchesList(): Unit =
      webClient.get.callRemoteAPI(GetSavedMatchAPI, None)


    override def handleMatchReplay(gameToReplay: Game): Unit = {
      guiStack.setCurrentScene(GameStage, gameController)
      gameController.createReplayActor(this.playerName, actorSystem.get, gameToReplay)
    }

    /**
      * Notify to whole system that a game is finished.
      */
    override def notifyGameFinished(): Unit = {
      guiStack.restorePreviousScene()
      socialController match {
        case Some(controller) => controller.finishGame()
        case None =>
      }
    }

    override def displayCurrentMatchesList(playedMatches: List[MatchRef]): Unit = socialController match {
      case Some(controller) => controller.getSocialGUIController.displayViewMatches(playedMatches.asJava)
      case None => genericGui.get.displayMatchesList(playedMatches.asJava)
    }

    /**
      * Start replay a played match.
      *
      * @param matchID ID of the match to replay.
      */
    override def startMatchReplay(matchID: String): Unit = {
      webClient.get.callRemoteAPI(GameRestAPI, None, matchID)
    }

    override def displayRegisteredMatchesList(playedMatches: List[MatchRef]): Unit = socialController match {
      case Some(controller) => controller.getSocialGUIController.displayReplayMatches(playedMatches.asJava)
      case None => genericGui.get.displayMatchesList(playedMatches.asJava)
    }

    override def startGenericGUI(): Unit = guiStack.setCurrentScene(GenericStage, this)

  }

}
