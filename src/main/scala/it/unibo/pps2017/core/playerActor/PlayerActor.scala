package it.unibo.pps2017.core.playerActor

import akka.actor.Actor
import akka.event.Logging

class PlayerActor extends Actor {

  val log = Logging(context.system, this)

  def receive: PartialFunction[Any, Unit] = {
    case "test" ⇒ log.info("received test")
    case _      ⇒ log.info("received unknown message")
  }
}
