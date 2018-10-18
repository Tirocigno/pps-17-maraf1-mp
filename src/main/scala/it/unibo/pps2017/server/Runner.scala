
package it.unibo.pps2017.server

import java.net.InetAddress

import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX


object Runner extends App {

  VERTX.deployVerticle(Dispatcher(
    AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
      "IP_SEEDS",
      "0",
      InetAddress.getLocalHost.getHostAddress)))
}

