package it.unibo.pps2017.client.model.remote
import akka.actor.ActorSystem
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.commons.remote.RestUtils.ServerContext
import it.unibo.pps2017.discovery.ServerDiscovery
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetServerAPI
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI$
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class RestWebClientTest extends FunSuite with BeforeAndAfterEach {

  val genericHost = "localhost"
  val discoveryPort = 2000
  val serverPort = 4700
  val defautlTimeOut = 5
  var vertx: Vertx = _
  val DEFAULT_PLAYERID = "C00lPlayer"
  var discoveryVerticle: ServerDiscovery = _
  var serverVerticle: Dispatcher = _
  var webClient: RestWebClient = _

  override def beforeEach() {
    vertx = Vertx.vertx()
    discoveryVerticle = ServerDiscovery(discoveryPort, defautlTimeOut)
    vertx.deployVerticle(discoveryVerticle)
    waitAsyncOpeartion
    serverVerticle = new Dispatcher(ActorSystem("dumpactor"))
    serverVerticle = new Dispatcher(ActorSystem("dumpactor"))
    vertx.deployVerticle(serverVerticle)
    waitAsyncOpeartion
    webClient = RestWebClient(ServerContext(genericHost, discoveryPort))
    waitAsyncOpeartion
  }

  override def afterEach() {
    vertx.close()
    waitAsyncOpeartion
    waitAsyncOpeartion
  }

  private def waitAsyncOpeartion: Unit = Thread.sleep(2000)

  test("testGetCurrentServerContext") {
    waitAsyncOpeartion // this waiting is necessary because the async operation is very heavy.
    webClient.callRemoteAPI(GetServerAPI, None)
    waitAsyncOpeartion
    assert(webClient.assignedServerContext.get == ServerContext(genericHost, serverPort))

  }

  test("testGetGameID") {
    waitAsyncOpeartion // this waiting is necessary because the async operation is very heavy.
    val map = Map(FoundGameRestAPI$.meParamKey -> DEFAULT_PLAYERID)
    webClient.callRemoteAPI(FoundGameRestAPI$, Some(map))
    waitAsyncOpeartion
    Thread.sleep(5000)
    assert(webClient.assignedServerContext.get == ServerContext(genericHost, serverPort))
  }

}
