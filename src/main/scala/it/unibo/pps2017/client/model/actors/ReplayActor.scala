
package it.unibo.pps2017.client.model.actors

import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.server.model.Game

class ReplayActor(override val controller: GameController, username: String, game: Game) extends ClientGameActor {

  var user: String = username


  def receive: PartialFunction[Any, Unit] = {

    case _ =>

  }



  override
  def getUsername: String = {
    user
  }

}
