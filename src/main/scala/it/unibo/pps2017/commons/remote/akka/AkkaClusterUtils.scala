package it.unibo.pps2017.commons.remote.akka

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object AkkaClusterUtils {

  def startSeedCluster: Unit = {
    val ports = Set("2551", "2552")
    ports foreach {
      startJoiningActorSystem("DiscoverySystem", _)
    }
  }

  def startJoiningActorSystem(systemName: String, port: String): ActorSystem = {
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())
    println("Config loaded")
    val actorsystem = ActorSystem(systemName, config)
    println("ActorSystem created")
    actorsystem
  }

}
