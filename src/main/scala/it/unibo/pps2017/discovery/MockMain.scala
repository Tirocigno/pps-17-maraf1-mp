
package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx


object MockMain extends App {
  Vertx.vertx().deployVerticle(ServerDiscovery())
}
