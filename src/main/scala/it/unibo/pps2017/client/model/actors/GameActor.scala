
package it.unibo.pps2017.client.model.actors

import it.unibo.pps2017.client.model.actors.playerActor.GameController

/**
  * Trait PlayerActor outline the model for real time comunication structure,
  * based on akka actors, used for playing
  * a remote Maraphone match.
  */
//TODO MERGE WITH BRASINI WORK.
trait GameActor extends ModelActor {
  override val controller: GameController
  def getUsername: String
}
