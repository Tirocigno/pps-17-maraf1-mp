
package it.unibo.pps2017.server

import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.controller.Dispatcher.VERTX
import it.unibo.pps2017.server.model.database.RedisConnection
import org.rogach.scallop.ScallopConf


class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val discoveryaddress = opt[String]()
  val myip = opt[String]()
  val redishost = opt[String]()
  val redisport = opt[String]()
  val redispw = opt[String]()
  verify()
}


object Runner extends App {

  val conf = new Conf(args) // Note: This line also works for "object Main extends App"
  var discovery = Dispatcher.DISCOVERY_URL
  var myip = Dispatcher.MY_IP

  if (conf.discoveryaddress.supplied) {
    Dispatcher.setDiscovery(conf.discoveryaddress())
  }
  if (conf.myip.supplied) {
    Dispatcher.setMyIp(conf.myip())
  }

  if (conf.redishost.supplied) {
    RedisConnection.setRedisHost(conf.redishost())
  }

  if (conf.redisport.supplied) {
    RedisConnection.setRedisPort(conf.redisport().toInt)
  }

  if (conf.redishost.supplied) {
    RedisConnection.setRedisPw(conf.redispw())
  }


  VERTX.deployVerticle(Dispatcher(
    AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
      Dispatcher.DISCOVERY_URL,
      "0",
      Dispatcher.MY_IP)))
}

