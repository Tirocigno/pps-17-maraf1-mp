package it.unibo.pps2017.client.model.actors.playeractor

import it.unibo.pps2017.client.controller.MatchController
import it.unibo.pps2017.client.model.actors.ModelActor

/**
  * Trait PlayerActor outline the model for real time comunication structure,
  * based on akka actors, used for playing
  * a remote Maraphone match.
  */
//TODO MERGE WITH BRASINI WORK.
trait PlayerActor extends ModelActor {
  override val controller: MatchController
}
