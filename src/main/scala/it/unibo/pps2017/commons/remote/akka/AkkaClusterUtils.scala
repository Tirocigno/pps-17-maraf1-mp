package it.unibo.pps2017.commons.remote.akka

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object AkkaClusterUtils {

  /**
    * Standard name for actorsystems, must be the same on all the actorsystem.
    */
  val STANDARD_SYSTEM_NAME = "Maraph1System"
  val STANDARD_HOST = "192.168.1.103"

  /**
    * Create the two nodes for the cluster.
    */
  def startSeedCluster(): Unit = {
    val ports = Set("2551", "2552")
    ports foreach startJoiningActorSystem
  }

  /**
    * Create a new node which will join the cluster.
    *
    * @param port the port on which the cluster will be run.
    * @return an actorsystem joined to the cluster.
    */
  def startJoiningActorSystem(port: String): ActorSystem = {
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())
    ActorSystem(STANDARD_SYSTEM_NAME, config)
  }

  /**
    * Create a new actorsystem, running on a random port.
    *
    * @return an actorsystem which will eventually join the cluster.
    */
  def startJoiningActorSystemOnRandomPort(): ActorSystem = startJoiningActorSystemWithRemoteSeed(STANDARD_HOST, "0")

  /**
    * Create a new node which will join the cluster.
    *
    * @param port the port on which the cluster will be run.
    * @return an actorsystem joined to the cluster.
    */
  def startJoiningActorSystemWithRemoteSeed(host: String, port: String): ActorSystem = {
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        cluster.seed-nodes = ["akka://Maraph1System@$host:2551","akka://Maraph1System@$host:2552"]
        """).withFallback(ConfigFactory.load())
    ActorSystem(STANDARD_SYSTEM_NAME, config)
  }

}
