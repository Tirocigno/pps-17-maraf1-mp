
package it.unibo.pps2017.client.controller

import akka.actor.ActorSystem
import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.core.gui.PlayGameController

sealed trait ClientController {

  def notifyError(throwable: Throwable)

  def setPlayGameController(guiController: PlayGameController)

}

object ClientController {

  private val staticController = new ClientControllerImpl

  /**
    * Apply is not used in order to mantain the singleton reference outside the object implementation.
    *
    * @return the singleton clientController
    */
  def getSingletonController: ClientControllerImpl = staticController

  class ClientControllerImpl() extends ClientController {
    val gameController = new GameController()
    private val actorSystem = ActorSystem("ClientActorSystem")
    gameController.createActor("player:14648511988945088", actorSystem)
    println("Creation endede")

    override def notifyError(throwable: Throwable): Unit = ???

    override def setPlayGameController(guiController: PlayGameController): Unit =
      gameController.playGameController = guiController

  }
}
