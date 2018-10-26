
package it.unibo.pps2017.client.controller
import akka.actor.{ActorRef, ActorSystem}
import it.unibo.pps2017.client.model.actors.ActorMessage

class ReplayController extends ActorController {

  override var currentActorRef: ActorRef = _

  override def createActor(actorId: String, actorSystem: ActorSystem): Unit = ???

  override def updateGUI(message: ActorMessage): Unit = ???
}
