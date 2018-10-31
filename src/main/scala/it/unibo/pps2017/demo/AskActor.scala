package it.unibo.pps2017.demo

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Await

case object AskNameMessage



class TestActor extends Actor {

  def receive = {
    case AskNameMessage => // respond to the "ask" request
      sender ! "Fred"
    case _ => println("that was unexpected")
  }
}



object AskActor extends App {

  // create the system and actor
  val system = ActorSystem("AskTestSystem")
  val myActor = system.actorOf(Props[TestActor], name = "myActor")

  // (1) this is one way to "ask" another actor
  implicit val timeout = Timeout(5 seconds)
  val future = myActor ? AskNameMessage
  val result = Await.result(future, timeout.duration).asInstanceOf[String]
  println(result)
}