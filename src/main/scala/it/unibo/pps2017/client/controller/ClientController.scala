
package it.unibo.pps2017.client.controller

import java.net.InetAddress

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.remote.RestWebClient
import it.unibo.pps2017.commons.remote.RestUtils.{IPAddress, Port, ServerContext}
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.core.gui.PlayGameController
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI$


sealed trait ClientController {

  def notifyError(throwable: Throwable)

  def setPlayGameController(guiController: PlayGameController)

  def setGameID(gameID: String)

  /**
    * Start an actorsystem which will join the seed disposed by the discovery.
    *
    * @param seedHost
    */
  def startActorSystem(seedHost: IPAddress)

  def setPlayerName(playerName: String)

  def createRestClient(discoveryIP: String, discoveryPort: Port)

  def sendMatchRequest(): Unit

  def getGameController: GameController

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
    var playerName: String = getRandomID
    private var actorSystem: Option[ActorSystem] = None
    var webClient: Option[RestWebClient] = None

    override def notifyError(throwable: Throwable): Unit = {
      throwable.printStackTrace()
    }

    override def setPlayGameController(guiController: PlayGameController): Unit =
      gameController.playGameController = guiController

    override def setGameID(gameID: String): Unit = gameController.joinPlayerToMatch(gameID)

    override def startActorSystem(seedHost: IPAddress): Unit = {
      val localIpAddress: String = InetAddress.getLocalHost.getHostAddress
      actorSystem = Some(AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(seedHost, "0", "192.168.1.14"))
      gameController.createActor(this.playerName, actorSystem.get)
    }

    override def setPlayerName(playerName: String): Unit = this.playerName = playerName

    override def createRestClient(discoveryIP: String, discoveryPort: Port): Unit = {
      webClient = Some(RestWebClient(ServerContext(discoveryIP, discoveryPort)))
    }

    override def sendMatchRequest(): Unit = {
      val map = Map(FoundGameRestAPI$.meParamKey -> playerName)
      webClient.get.callRemoteAPI(FoundGameRestAPI$, Some(map))
    }

    override def getGameController: GameController = gameController

  }
}
