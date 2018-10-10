
package it.unibo.pps2017.discovery

import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterServerAPI
import it.unibo.pps2017.server.model.ResponseStatus
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class ServerDiscoveryTest extends FunSuite with BeforeAndAfterEach {

  val defaultDiscoveryPort: Int = 4700
  val defaultHost: String = "localhost"
  val defaultPort: Int = 8080
  val otherPort: Int = 8081
  val timeOut: Int = 3
  val serverDiscovery: ServerDiscovery = ServerDiscovery(defaultDiscoveryPort, timeOut)
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
    val result = Await.result(request, timeOut seconds)
    assert(result.statusCode() == ResponseStatus.OK_CODE)
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
