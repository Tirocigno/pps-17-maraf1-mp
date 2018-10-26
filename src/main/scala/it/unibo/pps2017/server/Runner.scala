
package it.unibo.pps2017.server

import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX


object Runner extends App {


  VERTX.deployVerticle(Dispatcher(
    AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
      Dispatcher.DISCOVERY_URL,
      "0",
      Dispatcher.MY_IP)))
}

