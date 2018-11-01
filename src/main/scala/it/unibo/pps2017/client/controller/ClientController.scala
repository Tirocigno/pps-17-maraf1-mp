
package it.unibo.pps2017.client.controller

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.remote.{GameRestWebClient, RestWebClient}
import it.unibo.pps2017.client.view.{GenericGUIController, GuiStack}
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.commons.remote.rest.RestUtils.{IPAddress, MatchRef, Port, ServerContext}
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI


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
  def setCurrentGUI(gui: GenericGUIController): Unit


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
    */
  def sendMatchRequest(): Unit

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
    * @param userName the userName notify by the server.
    */
  def handleLoginAndRegistrationResponse(userName: String): Unit

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
    var actorSystem: Option[ActorSystem] = None
    var webClient: Option[RestWebClient] = None
    var socialController: Option[SocialController] = None

    override def notifyError(throwable: Throwable): Unit = {
      throwable.printStackTrace()
    }

    /*override def setGameID(gameID: String): Unit = {
      guiStack.setCurrentScene(GameStage, gameController)
      gameController.joinPlayerToMatch(gameID)
    }*/

    override def startActorSystem(seedHost: IPAddress, myIP: IPAddress): Unit = {
      actorSystem = Some(AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(seedHost, "0", myIP))
      gameController.createActor(this.playerName, actorSystem.get)
    }


    override def createRestClient(discoveryIP: String, discoveryPort: Port): Unit = {
      webClient = Some(GameRestWebClient(ServerContext(discoveryIP, discoveryPort)))
    }

    override def sendMatchRequest(): Unit = {
      val map = Map(FoundGameRestAPI.meParamKey -> playerName)
      webClient.get.callRemoteAPI(FoundGameRestAPI, Some(map))
    }


    override def setCurrentGUI(gui: GenericGUIController): Unit = ???

    /**
      * Start the game GUI and notify GameActor and SocialActor the player has joined a match.
      *
      * @param gameID a string containing the gameID, notify by the server.
      */
    override def handleMatchResponse(gameID: String): Unit = ???
  }
}
