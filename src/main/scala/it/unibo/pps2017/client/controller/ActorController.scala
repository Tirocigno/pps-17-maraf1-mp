
package it.unibo.pps2017.client.controller

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.model.actors.ActorMessage

/**
  * Trait to implement a controller dealing with an actor.
  */
trait ActorController extends Controller {
  var currentActorRef:ActorRef

  /**
    * Create an actor and set the actorRef inside the currentActorRef
    *
    * @param actorID     the id of the player to set inside the actor.
    * @param actorSystem the actorSystem to create the actor on.
    */
  def createActor(actorID: String, actorSystem: ActorSystem):Unit

  /**
    * Send a message to the actor.
    *
    * @param message the message to send.
    */
  def sendMessage(message:ActorMessage):Unit =
    currentActorRef ! message

  /**
    * Update the GUI based on the message processed.
    *
    * @param message the message to process.
    */
  def updateGUI(message: ActorMessage): Unit

}

