
package it.unibo.pps2017.discovery


import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.scala.core.{MultiMap, Vertx}
import io.vertx.scala.ext.web.client.{HttpRequest, WebClient, WebClientOptions}
import it.unibo.pps2017.commons.remote.rest.RestUtils.{IPAddress, MatchRef, Port}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI._
import it.unibo.pps2017.server.model.ResponseStatus
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

@RunWith(classOf[JUnitRunner])
class ServerDiscoveryTest extends FunSuite with BeforeAndAfterEach {

  val defaultDiscoveryPort: Int = 1234
  val defaultHost: String = "0.0.0.0"
  val defaultPort: Int = 8080
  val otherPort: Int = 8081
  val timeOut: Int = 5
  val asyncTimerDuration: Int = 1000
  val mockMatchRef:MatchRef = "MockMatch"
  var serverDiscovery: ServerDiscovery = ServerDiscovery(defaultDiscoveryPort, timeOut)
  private var vertx = Vertx.vertx()


  /**
    * Create the vertx environment every test.
    */
  override protected def beforeEach() {
    vertx = Vertx.vertx()
    serverDiscovery = ServerDiscovery(defaultDiscoveryPort, timeOut)
    vertx.deployVerticle(serverDiscovery)
    waitForAsyncMethodToExecute()
  }

  /**
    * Shut down the server after exh test.
    */
  override protected def afterEach(): Unit = {
    vertx.close()
    waitForAsyncMethodToExecute()
  }

  /**
    * This method is used to block the thread to wait for a async call effect.
    */
  private def waitForAsyncMethodToExecute(): Unit = {
    Thread.sleep(asyncTimerDuration)
  }

  private def generateIPMultiMap(port: Port): MultiMap = {
    val map = MultiMap caseInsensitiveMultiMap()
    map.add(StandardParameters.IP_KEY, defaultHost)
    map.add(StandardParameters.PORT_KEY, port.toString)
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

  private def registerAServer(webClient: WebClient, port: Port, params: MultiMap) = {
    executeAPICallAndWait(webClient, port, defaultHost, RegisterServerAPI, params)
  }

  private def executeAPICallAndWait(request: HttpRequest[Buffer], paramMap: MultiMap) = {
    Await.result(request.sendFormFuture(paramMap), timeOut seconds)
  }

  private def executeAPICallAndWait(webClient: WebClient, port: Port, host: IPAddress, api: DiscoveryAPI,
                                    params: MultiMap) = {
    api.httpMethod match {
      case HttpMethod.POST => Await.result(webClient.post(port, host, api.path).sendFormFuture(params), timeOut seconds)
      case HttpMethod.GET => Await.result(webClient.get(port, host, api.path).sendFormFuture(params), timeOut seconds)
      case _ => fail()
    }
  }


  test("Adding a server to  server Discovery") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
  }


  test("Getting a registered server") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val getResult = executeAPICallAndWait(webClient, defaultDiscoveryPort, defaultHost, GetServerAPI,
      generateIPMultiMap(defaultPort))
    assert(getResult.statusCode() == ResponseStatus.OK_CODE)
  }

  test("Trying to get a server when no server is registered") {
    val webClient = generateMockClient(defaultPort)
    val increaseResult = executeAPICallAndWait(webClient, defaultDiscoveryPort, defaultHost, GetServerAPI,
      generateIPMultiMap(defaultPort))
    assert(increaseResult.statusCode() == ResponseStatus.EXCEPTION_CODE)
  }


  test("Increasing number of matches on a server") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val increaseResult = executeAPICallAndWait(webClient,
      defaultDiscoveryPort, defaultHost, IncreaseServerMatchesAPI, generateIPMultiMap(defaultPort))
    assert(increaseResult.statusCode() == ResponseStatus.OK_CODE)
  }

  test("Increasing number of matches on a non registered server") {
    val webClient = generateMockClient(defaultPort)
    val increaseResult = executeAPICallAndWait(webClient,
      defaultDiscoveryPort, defaultHost, IncreaseServerMatchesAPI, generateIPMultiMap(defaultPort))
    assert(increaseResult.statusCode() == ResponseStatus.EXCEPTION_CODE)
  }

  test("Decreasing number of matches on a server") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val increaseResult = executeAPICallAndWait(webClient,
      defaultDiscoveryPort, defaultHost, IncreaseServerMatchesAPI, generateIPMultiMap(defaultPort))
    val decreaseResult = executeAPICallAndWait(webClient,
      defaultDiscoveryPort, defaultHost, DecreaseServerMatchesAPI, generateIPMultiMap(defaultPort))
    assert(increaseResult.statusCode() == ResponseStatus.OK_CODE)
  }

  test("Decreasing number of matches on a server with no matches") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val decreaseResult = executeAPICallAndWait(webClient,
      defaultDiscoveryPort, defaultHost, DecreaseServerMatchesAPI, generateIPMultiMap(defaultPort))
    assert(decreaseResult.statusCode() == ResponseStatus.EXCEPTION_CODE)
  }

  test("Decreasing number of matches on a not registered server") {
    val webClient = generateMockClient(defaultPort)
    val decreaseResult = executeAPICallAndWait(webClient,
      defaultDiscoveryPort, defaultHost, DecreaseServerMatchesAPI, generateIPMultiMap(defaultPort))
    assert(decreaseResult.statusCode() == ResponseStatus.EXCEPTION_CODE)
  }

  test("Get an empty set of matches") {
    val webClient = generateMockClient(defaultPort)
    val callResult = executeAPICallAndWait(webClient, defaultDiscoveryPort,
      defaultHost, GetAllMatchesAPI, generateIPMultiMap(defaultPort))
    assert(callResult.statusCode() == ResponseStatus.OK_CODE)
  }

  test("Register a match and retrieve the non empty list") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val restCall = webClient.post(defaultDiscoveryPort,defaultHost,
      RegisterMatchAPI.path)
    val paramMap = MultiMap.caseInsensitiveMultiMap()
    paramMap.add(RegisterMatchAPI.MATCH_ID_KEY, mockMatchRef)
    paramMap.addAll(generateIPMultiMap(defaultPort))
    val insertResult = executeAPICallAndWait(restCall, paramMap)
    assert(insertResult.statusCode() == ResponseStatus.OK_CODE)
    val callResult = executeAPICallAndWait(webClient, defaultDiscoveryPort,
      defaultHost, GetAllMatchesAPI, generateIPMultiMap(defaultPort))
    assert(callResult.statusCode() == ResponseStatus.OK_CODE)
  }

  test("Register a match and delete it") {
    val webClient = generateMockClient(defaultPort)
    val result = registerAServer(webClient, defaultDiscoveryPort, generateIPMultiMap(defaultPort))
    assert(result.statusCode() == ResponseStatus.OK_CODE)
    val insertCall = webClient.post(defaultDiscoveryPort,defaultHost,
      RegisterMatchAPI.path)
    val paramMap = MultiMap.caseInsensitiveMultiMap()
    paramMap.add(RegisterMatchAPI.MATCH_ID_KEY, mockMatchRef)
    paramMap.addAll(generateIPMultiMap(defaultPort))
    val insertResult = executeAPICallAndWait(insertCall, paramMap)
    assert(insertResult.statusCode() == ResponseStatus.OK_CODE)
    val removeCall = webClient.post(defaultDiscoveryPort,defaultHost,
      RemoveMatchAPI.path)
    val removeResult = executeAPICallAndWait(removeCall, paramMap)
    assert(removeResult.statusCode() == ResponseStatus.OK_CODE)
  }

}
