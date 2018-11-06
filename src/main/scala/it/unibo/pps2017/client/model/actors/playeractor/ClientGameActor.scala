package it.unibo.pps2017.client.model.actors.playeractor

import it.unibo.pps2017.client.controller.actors.playeractor.GameController
import it.unibo.pps2017.client.model.actors.ModelActor

/**
  * Trait ClientGameActor outline the model for real time communication structure,
  * based on akka actors, used for playing a Marafone game.
  */

trait ClientGameActor extends ModelActor {
  override val controller: GameController
}
