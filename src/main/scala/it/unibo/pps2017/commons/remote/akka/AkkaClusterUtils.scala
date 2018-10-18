package it.unibo.pps2017.commons.remote.akka

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object AkkaClusterUtils {

  /**
    * Standard name for actorsystems, must be the same on all the actorsystem.
    */
  val STANDARD_SYSTEM_NAME = "Maraph1System"

  /**
    * Create the two nodes for the cluster.
    */
  def startSeedCluster(currentHost: String): Unit = {
    val ports = Set("2551", "2552")
    ports foreach { port =>
      startJoiningActorSystem(port, currentHost)
    }
  }

  /**
    * Create a new node which will join the cluster.
    *
    * @param port the port on which the cluster will be run.
    * @return an actorsystem joined to the cluster.
    */
  def startJoiningActorSystem(port: String, currentHost: String): ActorSystem = {
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.hostname = "$currentHost"
        akka.remote.netty.tcp.port=$port
        akka.cluster.seed-nodes = ["akka.tcp://Maraph1System@$currentHost:2551","akka.tcp://Maraph1System@$currentHost:2552"]
        """).withFallback(ConfigFactory.load())
    ActorSystem(STANDARD_SYSTEM_NAME, config)
  }



  /**
    * Create a new node which will join the cluster.
    *
    * @param port the port on which the cluster will be run.
    * @return an actorsystem joined to the cluster.
    */
  def startJoiningActorSystemWithRemoteSeed(remoteHost: String, port: String, myIP: String): ActorSystem = {
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.hostname = "$myIP"
        akka.remote.netty.tcp.port=$port
        akka.cluster.seed-nodes = ["akka.tcp://Maraph1System@$remoteHost:2551","akka.tcp://Maraph1System@$remoteHost:2552"]
        """).withFallback(ConfigFactory.load())
    ActorSystem(STANDARD_SYSTEM_NAME, config)
  }

}
