
package it.unibo.pps2017.demo

import org.rogach.scallop._

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val apples = opt[Int]()
  val bananas = opt[Int]()
  val discoveryaddress = opt[String]()
  val discoveryport = opt[String]()
  // val name = trailArg[String]()
  verify()
}

object ScalaDemo extends App {
  val conf = new Conf(args) // Note: This line also works for "object Main extends App"
  if (conf.discoveryaddress.supplied) {
    println("Horray! Address supplied: " + conf.discoveryaddress())
  }
  if (conf.discoveryport.supplied) {
    println("Horray! Port supplied: " + conf.discoveryport())
  }
}
