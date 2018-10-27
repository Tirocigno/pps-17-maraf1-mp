package it.unibo.pps2017.demo

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

case object Swap

case object Swap1
case object Swap2

class Swapper extends Actor {
  import context._
  val log = Logging(system, this)

  def receive: PartialFunction[Any, Unit] = {
    case Swap ⇒
      log.info("Hi")
      become({
        case Swap1 =>
          log.info("Hoooo")
        case Swap2 =>
          log.info("Heeee")
          //unbecome() // resets the latest 'become' (just for fun)
      }, discardOld = true) // push on top instead of replace
  }
}

object SwapperApp extends App {
  val system = ActorSystem("SwapperSystem")
  val swap = system.actorOf(Props[Swapper], name = "swapper")
  swap ! Swap1 // non viene considerato
  swap ! Swap  // logs Hi
  swap ! Swap1// logs Hoooo
  swap ! Swap1  // logs Hoooo
  swap ! Swap2  // logs Heeee
}
