package it.unibo.pps2017.demo

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import it.unibo.pps2017.client.model.actors.playeractor.ClientMessages.{CardOk, ClickedCard}

import scala.concurrent.Await
import scala.concurrent.duration._

case object AskNameMessage



class TestActor extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case ClickedCard(_, _) => // respond to the "ask" request
      sender ! CardOk(correctClickedCard = true, "nic")
    case _ => println("that was unexpected")
  }
}



object AskActor extends App {

  // create the system and actor
  val system = ActorSystem("AskTestSystem")
  val myActor = system.actorOf(Props[TestActor], name = "myActor")

  // (1) this is one way to "ask" another actor
  implicit val timeout: Timeout = Timeout(1.nanosecond)
  val future = myActor ? ClickedCard(1, "nic")

  val result = Await.result(future, timeout.duration).asInstanceOf[CardOk]
  println("RISULTATO: " + result.correctClickedCard + " "+ result.player)
}