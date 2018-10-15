package it.unibo.pps2017.client.controller.actors

import akka.actor.ActorRef

package object playeractor {

  val noActorFoundMessage = "No ActorRef found"

  implicit def getOrThrow(actorRef: Option[ActorRef]): ActorRef = {
    actorRef.getOrElse(throw new NoSuchElementException(noActorFoundMessage))
  }
}
