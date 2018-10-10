
package it.unibo.pps2017.discovery

import io.vertx.core.http.HttpMethod
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{DiscoveryAPI, GetServerAPI, RegisterServerAPI}
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
  var serverDiscovery: ServerDiscovery = ServerDiscovery(defaultDiscoveryPort, timeOut)
  private var vertx = Vertx.vertx()
  vertx.deployVerticle(serverDiscovery)

  override protected def beforeEach() {
    vertx = Vertx.vertx()
    vertx.deployVerticle(serverDiscovery)
  }

  private def generateMockClient(port: Int): WebClient = {
    val options = WebClientOptions() setDefaultPort defaultPort
    WebClient.create(vertx, options)
  }

  //TODO MUST DO HIS JOB
  override protected def afterEach(): Unit = {
    vertx.close()
  }

  private def registerAServer(webClient: WebClient, port: Port) = {
    executeAPICallAndWait(webClient, port, defaultHost, RegisterServerAPI)
  }

  private def executeAPICallAndWait(webClient: WebClient, port: Port, host: IPAddress, api: DiscoveryAPI) = {
    api.httpMethod match {
      case HttpMethod.POST => Await.result(webClient.post(port, host, api.path).sendFuture(), timeOut seconds)
      case HttpMethod.GET => Await.result(webClient.get(port, host, api.path).sendFuture(), timeOut seconds)
      case _ => fail()
    }
  }


  test("Adding a server to  server Discovery") {
    println("Test one started")
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort)
    assert(result.statusCode() == ResponseStatus.OK_CODE)

  }

  //TODO Test should not pass, instead pass because afterEach doesn't do his job
  test("Increasing number of current played matches on a registered server") {
    println("Test two started")
    val webClient = generateMockClient(defaultPort)
    /* val result = registerAServer(webClient, defaultDiscoveryPort)*/
    //assert(result.statusCode() == ResponseStatus.OK_CODE)
    val increaseResult = executeAPICallAndWait(webClient, defaultDiscoveryPort, defaultHost, GetServerAPI)
    assert(increaseResult.statusCode() == ResponseStatus.OK_CODE)
  }

  //TODO Increase is probably broken, FIX IT
  test("Mock test 3") {
    Thread.sleep(1000)
  }
  test("Mock test 4") {
    Thread.sleep(1000)
  }

}
