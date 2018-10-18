
package it.unibo.pps2017.server

import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX
import it.unibo.pps2017.server.model.ClusterUtils


object Runner extends App {

  VERTX.deployVerticle(Dispatcher(ClusterUtils()))
}

