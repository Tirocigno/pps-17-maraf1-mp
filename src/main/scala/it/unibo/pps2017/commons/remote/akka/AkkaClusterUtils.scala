package it.unibo.pps2017.commons.remote.akka

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object AkkaClusterUtils {

  def startSeedCluster: Unit = {
    val ports = Set(2551, 2551)
    ports foreach {
      startJoiningActorSystem("DiscoverySystem", _)
    }
  }

  def startJoiningActorSystem(systemName: String, port: Int): ActorSystem = {
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())
    ActorSystem(systemName, config)
  }

}
