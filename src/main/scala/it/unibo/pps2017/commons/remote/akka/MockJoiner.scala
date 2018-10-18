package it.unibo.pps2017.commons.remote.akka

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe

object MockJoiner extends App {
  val actor = AkkaClusterUtils.startJoiningActorSystem("0")


  val actorRef = actor.actorOf(Props[PongoActorResponder])
}

class PongoActorResponder extends Actor {

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("Il regno del pongo", self)
  println("Subscription done")

  override def receive: Receive = {
    case PongoMessage(_, sender) => sender ! PongoResponse()
  }
}
