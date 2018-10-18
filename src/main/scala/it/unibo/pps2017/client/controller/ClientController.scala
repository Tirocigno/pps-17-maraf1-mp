
package it.unibo.pps2017.client.controller

import java.net.InetAddress

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.commons.remote.RestUtils.IPAddress
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.core.gui.PlayGameController

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

    override def notifyError(throwable: Throwable): Unit = ???

    override def setPlayGameController(guiController: PlayGameController): Unit =
      gameController.playGameController = guiController

    override def setGameID(gameID: String): Unit = gameController.joinPlayerToMatch(gameID)

    override def startActorSystem(seedHost: IPAddress): Unit = {
      val localIpAddress: String = InetAddress.getLocalHost.getHostAddress
      actorSystem = Some(AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(seedHost, "0", localIpAddress))
      gameController.createActor(this.playerName, actorSystem.get)
    }

    override def setPlayerName(playerName: String): Unit = this.playerName = playerName

  }
}
