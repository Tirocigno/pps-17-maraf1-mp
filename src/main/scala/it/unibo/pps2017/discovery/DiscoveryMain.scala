package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx


object DiscoveryMain extends App {

  val port = 2000

  val timeOut = 10

  val discovery: ServerDiscovery = ServerDiscovery(port, timeOut)

  Vertx.vertx().deployVerticle(discovery)

  discovery.startAkkaCluster("192.168.1.14")
}
