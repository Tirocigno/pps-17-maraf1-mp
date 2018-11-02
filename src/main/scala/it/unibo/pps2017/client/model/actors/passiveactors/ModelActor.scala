
package it.unibo.pps2017.client.model.actors.passiveactors

import akka.actor.Actor
import it.unibo.pps2017.client.controller.ActorController

/**
  * This trait models a contract between all the actors running on client:
  * Every actor must have a controller object inside in order to communicate with the gui.
  */
trait ModelActor extends Actor {
  /**
    * Every actor should have a reference to the controller that create it.
    */
 val controller:ActorController

  /**
    * Name of the actor.
    *
    * @return the name of the actor
    */
  def username: String
}

/**
  * Trait to be extended by all the messages received by the client actor.
  */
trait ActorMessage
