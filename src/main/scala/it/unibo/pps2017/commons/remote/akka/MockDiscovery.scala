package it.unibo.pps2017.commons.remote.akka

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils.STANDARD_SYSTEM_NAME

object MockDiscovery extends App {

  AkkaClusterUtils.startSeedCluster

  val actorsystem:ActorSystem =  ActorSystem(STANDARD_SYSTEM_NAME)//AkkaClusterUtils.startJoiningActorSystemOnRandomPort("192.168.5.5")

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