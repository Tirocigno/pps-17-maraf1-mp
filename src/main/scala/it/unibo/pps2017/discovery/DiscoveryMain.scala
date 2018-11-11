package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx
import org.rogach.scallop.ScallopConf

/**
  * Class to be used to parse CLI commands, the values declared inside specify name and type of the arguments to parse.
  *
  * @param arguments the programs arguments as an array of strings.
  */
class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val myip = opt[String]()
  val myport = opt[Int]()
  verify()
}


object DiscoveryMain extends App {

  val conf = new Conf(args) // Note: This line also works for "object Main extends App"
  var port = 2000
  var discoveryAddress = "127.0.0.1"

  if (conf.myip.supplied) {
    discoveryAddress = conf.myip()
  }

  if (conf.myport.supplied) {
    port = conf.myport()
  }

  val timeOut = 10

  val discovery: ServerDiscovery = ServerDiscovery(port, timeOut)

  Vertx.vertx().deployVerticle(discovery)

  discovery.startAkkaCluster(discoveryAddress)
}
