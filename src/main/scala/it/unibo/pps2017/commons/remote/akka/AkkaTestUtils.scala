
package it.unibo.pps2017.commons.remote.akka

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object AkkaTestUtils {

  /**
    * Generate a isolated actorsytem which does not login to any cluster.
    *
    * @return an isolated actorsytem, useful for test purpouse.
    */
  def generateTestActorSystem(): ActorSystem =
    ActorSystem("DumpSystem", ConfigFactory.load("redisConf"))
}
