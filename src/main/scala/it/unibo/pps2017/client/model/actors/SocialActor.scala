
package it.unibo.pps2017.client.model.actors

import it.unibo.pps2017.client.controller.SocialController

/**
  * The socialActor will be responsable of all the function in which real time
  * connection is necessary, such as sending and receiving friendship and
  * challenge requests.
  */
//TODO IMPLEMENT THIS ACTOR.
trait SocialActor extends ModelActor {
  override val controller: SocialController
}
