
package it.unibo.pps2017.client.model.actors

import akka.actor.Actor
import it.unibo.pps2017.client.controller.ActorController

trait ModelActor extends Actor {
 val controller:ActorController
}

trait ActorMessage
