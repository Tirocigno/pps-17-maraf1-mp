
package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx


object MockMain extends App {
  val serverDiscovery = ServerDiscovery(4700, 3)
  serverDiscovery.addMockServer("Autunno", 2018)
  Vertx.vertx().deployVerticle(serverDiscovery)
}
