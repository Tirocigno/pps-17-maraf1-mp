
package it.unibo.pps2017.server
import controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX



object Runner extends App {

  VERTX.deployVerticle(new Dispatcher())
}

