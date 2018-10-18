
package it.unibo.pps2017.server

import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX


object Runner extends App {


  VERTX.deployVerticle(Dispatcher(
    AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
      "192.168.1.103",
      "0",
      "192.168.1.103")))
}

