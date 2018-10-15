
package it.unibo.pps2017.client.controller

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.model.actors.ActorMessage

trait ActorController {
  var currentActorRef:ActorRef

  def createActor(actorID: String, actorSystem: ActorSystem):Unit

  def sendMessage(message:ActorMessage):Unit =
    currentActorRef ! message

  def updateGUI(message: ActorMessage): Unit
}

