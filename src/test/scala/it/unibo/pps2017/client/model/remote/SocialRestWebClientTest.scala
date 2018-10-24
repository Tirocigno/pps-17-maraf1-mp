
package it.unibo.pps2017.client.model.remote

import akka.actor.{ActorRef, ActorSystem}
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.client.controller.SocialController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.commons.remote.rest.RestUtils.ServerContext
import it.unibo.pps2017.discovery.ServerDiscovery
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterSocialIDAPI
import it.unibo.pps2017.discovery.structures.SocialActorsMap.SocialMap
import it.unibo.pps2017.server.controller.Dispatcher
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class SocialRestWebClientTest extends FunSuite with BeforeAndAfterEach {

  val GENERIC_HOST = "127.0.0.1"
  val DISCOVERY_PORT = 2000
  val SERVER_PORT = 4700
  val ASYNC_PAUSE = 2000
  val defautlTimeOut = 5
  val controller: SocialController = MockSocialController()
  val DEFAULT_SOCIAL_ID: String = "Lorenzo_Valgimigli"
  val DEFAULT_ACTOR_REF: ActorRef = ActorRef.noSender
  var vertx: Vertx = _
  var discoveryVerticle: ServerDiscovery = _
  var serverVerticle: Dispatcher = _
  var webClient: RestWebClient = _
  var isClusterRunning = false

  override def beforeEach() {
    vertx = Vertx.vertx()
    discoveryVerticle = ServerDiscovery(DISCOVERY_PORT, defautlTimeOut)
    if (!isClusterRunning) {
      discoveryVerticle.startAkkaCluster(GENERIC_HOST)
      isClusterRunning = true
    }
    vertx.deployVerticle(discoveryVerticle)
    waitAsyncOperation()
    serverVerticle = Dispatcher(
      AkkaClusterUtils.startJoiningActorSystemWithRemoteSeed(
        GENERIC_HOST,
        "0",
        GENERIC_HOST))
    vertx.deployVerticle(serverVerticle)
    waitAsyncOperation()
    webClient = SocialRestWebClient(controller, ServerContext(GENERIC_HOST, DISCOVERY_PORT))
    waitAsyncOperation()
  }

  private def waitAsyncOperation(): Unit = Thread.sleep(ASYNC_PAUSE)

  override def afterEach() {
    vertx.close()
    waitAsyncOperation()
  }

  test("Register new ID to social Actor") {
    val encodedActorRef: String = ""
    val map = Map(RegisterSocialIDAPI.SOCIAL_ID -> DEFAULT_SOCIAL_ID,
      RegisterSocialIDAPI.SOCIAL_ACTOR -> DEFAULT_ACTOR_REF)
  }


}

private case class MockSocialController() extends SocialController {

  override var currentActorRef: ActorRef = _
  var bodyResponse: String = _
  var playerList: SocialMap = _

  override def notifyCallResultToGUI(message: Option[String]): Unit = bodyResponse = message.get

  override def setAndDisplayOnlinePlayerList(playerList: SocialMap): Unit = this.playerList = playerList

  override def createActor(actorID: String, actorSystem: ActorSystem): Unit = ???

  override def updateGUI(message: ActorMessage): Unit = ???

}
