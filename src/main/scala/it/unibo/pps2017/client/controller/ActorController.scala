
package it.unibo.pps2017.client.controller

import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.view.GUIController

trait ActorController {
  var currentActorRef:ActorRef
  var currentGUI: GUIController

  def createActor(actorID: String, actorSystem: ActorSystem):Unit

  def sendMessage(message:ActorMessage):Unit =
    currentActorRef ! message

  def updateGUI(message: ActorMessage): Unit

  def setGUI[A <: GUIController](gui: A): Unit = this.currentGUI = gui

}

