
package it.unibo.pps2017.server.model

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import it.unibo.pps2017.server.server.{AKKA_CLUSTER_PORT, AKKA_CONFIG_FILE, systemName}

object ClusterUtils {

  /**
    * Join the cluster and return the ActorSystem.
    *
    * @return
    * the ActorSystem.
    */
  def apply(): ActorSystem = {

    AKKA_CLUSTER_PORT match {
      case Some(port) =>
        val config = ConfigFactory.parseString(
          s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load(AKKA_CONFIG_FILE))

        ActorSystem(systemName, config)
      case None =>
        val config = ConfigFactory.load(AKKA_CONFIG_FILE)

        ActorSystem(systemName, config)
    }
  }
}
