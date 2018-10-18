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
  def startSeedCluster(): Unit = {
    val ports = Set("2551", "2552")
    ports foreach { port =>
      startJoiningActorSystem(port)
    }
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
        akka.remote.netty.tcp.hostname = "192.168.5.5"
        akka.remote.netty.tcp.port=$port
        akka.cluster.seed-nodes = ["akka.tcp://Maraph1System@192.168.5.5:2551","akka.tcp://Maraph1System@192.168.5.5:2552"]
        """).withFallback(ConfigFactory.load())
    ActorSystem(STANDARD_SYSTEM_NAME, config)
  }



  /**
    * Create a new node which will join the cluster.
    *
    * @param port the port on which the cluster will be run.
    * @return an actorsystem joined to the cluster.
    */
  def startJoiningActorSystemWithRemoteSeed(host: String, port: String, myIP: String): ActorSystem = {
    println(host)
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.hostname = "$myIP"
        akka.remote.netty.tcp.port=$port
        akka.cluster.seed-nodes = ["akka.tcp://Maraph1System@$host:2551","akka.tcp://Maraph1System@$host:2552"]
        """).withFallback(ConfigFactory.load())
    ActorSystem(STANDARD_SYSTEM_NAME, config)
  }

}
