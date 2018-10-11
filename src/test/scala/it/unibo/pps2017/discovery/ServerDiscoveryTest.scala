
package it.unibo.pps2017.discovery


import io.vertx.core.http.HttpMethod
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{DiscoveryAPI, GetServerAPI, IncreaseServerMatches, RegisterServerAPI}
import it.unibo.pps2017.server.model.ResponseStatus
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

@RunWith(classOf[JUnitRunner])
class ServerDiscoveryTest extends FunSuite with BeforeAndAfterEach {

  val defaultDiscoveryPort: Int = 4700
  val defaultHost: String = "localhost"
  val defaultPort: Int = 8080
  val otherPort: Int = 8081
  val timeOut: Int = 3
  var serverDiscovery: ServerDiscovery = ServerDiscovery(defaultDiscoveryPort, timeOut)
  private var vertx = Vertx.vertx()

  /**
    * Create the vertx environment every test.
    */
  override protected def beforeEach() {
    vertx = Vertx.vertx()
    serverDiscovery = ServerDiscovery(defaultDiscoveryPort, timeOut)
    vertx.deployVerticle(serverDiscovery)
  }

  /**
    * Shut down the server after exh test.
    */
  override protected def afterEach(): Unit = {
    vertx.close()
  }

  private def generateMockClient(port: Int): WebClient = {
    val options = WebClientOptions() setDefaultPort defaultPort
    WebClient.create(vertx, options)
  }

  private def executeAPICallAndWait(webClient: WebClient, port: Port, host: IPAddress, api: DiscoveryAPI) = {
    api.httpMethod match {
      case HttpMethod.POST => Await.result(webClient.post(port, host, api.path).sendFuture(), timeOut seconds)
      case HttpMethod.GET => Await.result(webClient.get(port, host, api.path).sendFuture(), timeOut seconds)
      case _ => fail()
    }
  }

  private def registerAServer(webClient: WebClient, port: Port) = {
    executeAPICallAndWait(webClient, port, defaultHost, RegisterServerAPI)
  }


  test("Adding a server to  server Discovery") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort)
    assert(result.statusCode() == ResponseStatus.OK_CODE)
  }


  test("Getting a registered server") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort)
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val increaseResult = executeAPICallAndWait(webClient, defaultDiscoveryPort, defaultHost, GetServerAPI)
    assert(increaseResult.statusCode() == ResponseStatus.OK_CODE)
  }

  test("Trying to get a server when no server is registered") {
    val webClient = generateMockClient(defaultPort)
    val increaseResult = executeAPICallAndWait(webClient, defaultDiscoveryPort, defaultHost, GetServerAPI)
    assert(increaseResult.statusCode() == ResponseStatus.EXCEPTION_CODE)
  }
  test("Increasing number of matches on a server") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort)
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val increaseResult = executeAPICallAndWait(webClient, defaultDiscoveryPort, defaultHost, IncreaseServerMatches)
    println(increaseResult.bodyAsString().get)
    assert(increaseResult.statusCode() == ResponseStatus.OK_CODE)
  }

}
