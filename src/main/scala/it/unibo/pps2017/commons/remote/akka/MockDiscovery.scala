package it.unibo.pps2017.commons.remote.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish

object MockDiscovery extends App {

  AkkaClusterUtils.startSeedCluster("mockdiscovery")

  val actorsystem: ActorSystem = AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed("", "0", "0")

  val actorRef = actorsystem.actorOf(Props[PongoActorDistributor], "Distributor")

  println(actorRef)

  while (true) {
    println("Timer invoked")
    Thread.sleep(5000)
    actorRef ! PongoMessage("", ActorRef.noSender)
  }


}

class PongoActorDistributor extends Actor with ActorLogging {

  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  log.info("Actor created")

  override def receive: Receive = {
    case PongoMessage(_, _) => log.info("Received message")
      mediator ! Publish("Il regno del pongo", PongoMessage("W " +
        "il " +
        "pongo", self))
    case PongoResponse() => println("Received pongoresponse")
  }
}


case class PongoMessage(content: String, sender: ActorRef)

case class PongoResponse()