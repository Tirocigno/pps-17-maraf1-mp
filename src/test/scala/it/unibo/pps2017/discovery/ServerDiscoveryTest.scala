
package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterServerAPI
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ServerDiscoveryTest extends FunSuite with BeforeAndAfterEach {

  val defaultDiscoveryPort: Int = 4700
  val defaultHost: String = "localhost"
  val serverDiscovery: ServerDiscovery = ServerDiscovery()
  val defaultPort: Int = 8080
  val otherPort: Int = 8081
  private val vertx = Vertx.vertx()

  private def generateMockClient(port: Int): WebClient = {
    val options = WebClientOptions() setDefaultPort defaultPort
    WebClient.create(vertx, options)
  }

  override def beforeEach() {
    vertx.deployVerticle(serverDiscovery)
  }

  test("Adding a server to  server Discovery") {
    val request = generateMockClient(defaultPort)
      .post(defaultPort, defaultHost, RegisterServerAPI.path)
      .sendFuture()
    val result = Await.result(request, 3 seconds)
    assert(result.statusCode() == 200)
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
