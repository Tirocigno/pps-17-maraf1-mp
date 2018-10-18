package it.unibo.pps2017.commons.remote.akka

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe

object MockJoiner extends App {
  val actor = AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed("192.168.5.6", "0")


  val actorRef = actor.actorOf(Props[PongoActorResponder])
}

class PongoActorResponder extends Actor {

  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("Il regno del pongo", self)
  println("Subscription done")

  override def receive: Receive = {
    case PongoMessage(_, sender) => sender ! PongoResponse()
  }
}
