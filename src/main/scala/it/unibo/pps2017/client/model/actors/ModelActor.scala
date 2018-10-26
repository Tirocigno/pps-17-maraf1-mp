
package it.unibo.pps2017.client.model.actors

import akka.actor.Actor
import it.unibo.pps2017.client.controller.ActorController

/**
  * This trait models a contract between all the actors running on client:
  * Every actor must have a controller object inside in order to communicate with the gui.
  */
trait ModelActor extends Actor {
 val controller:ActorController
}

/**
  * Trait to be extended by all the messages received by the client actor.
  */
trait ActorMessage
