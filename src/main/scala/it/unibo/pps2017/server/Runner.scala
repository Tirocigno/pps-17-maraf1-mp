
package it.unibo.pps2017.server

import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX
import org.rogach.scallop.ScallopConf


class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val discoveryaddress = opt[String]()
  val myip = opt[String]()
  verify()
}


object Runner extends App {

  val conf = new Conf(args) // Note: This line also works for "object Main extends App"
  var discovery = Dispatcher.DISCOVERY_URL
  var myip = Dispatcher.MY_IP

  if (conf.discoveryaddress.supplied) {
    discovery = conf.discoveryaddress()
  }
  if (conf.myip.supplied) {
    myip = conf.myip()
  }


  VERTX.deployVerticle(Dispatcher(
    AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
      discovery,
      "0",
      myip)))
}

