
package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class ServerDiscoveryTest extends FunSuite with BeforeAndAfterEach {

  val serverDiscovery: ServerDiscovery = ServerDiscovery()
  val defaultPort: Int = 8080
  val otherPort: Int = 8081
  private val vertx = Vertx.vertx()

  override def beforeEach() {
    vertx.deployVerticle(serverDiscovery)
  }

  test("Mock test 1") {
    Thread.sleep(1000)
  }

  test("Mock test 2") {
    Thread.sleep(1000)
  }

  test("Mock test 3") {
    Thread.sleep(1000)
  }
  test("Mock test 4") {
    Thread.sleep(1000)
  }

  override def afterEach() {
    vertx.close()
  }

}
