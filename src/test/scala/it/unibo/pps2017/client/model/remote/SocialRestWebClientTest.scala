
package it.unibo.pps2017.client.model.remote

import akka.actor.{ActorRef, ActorSystem}
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.client.controller.SocialController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.commons.remote.akka.AkkaTestUtils
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, serializeActorRef}
import it.unibo.pps2017.commons.remote.social.PartyRole
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, SocialMap}
import it.unibo.pps2017.discovery.ServerDiscovery
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{GetAllOnlinePlayersAPI, RegisterSocialIDAPI, UnregisterSocialIDAPI}
import it.unibo.pps2017.server.controller.Dispatcher
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class SocialRestWebClientTest extends FunSuite with BeforeAndAfterEach {

  val GENERIC_HOST = "127.0.0.1"
  val DISCOVERY_PORT = 2000
  val SERVER_PORT = 4700
  val ASYNC_PAUSE = 2000
  val defautlTimeOut = 5
  val controller: MockSocialController = new MockSocialController
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
    vertx.deployVerticle(discoveryVerticle)
    waitAsyncOperation()
    serverVerticle = Dispatcher(AkkaTestUtils.generateTestActorSystem())
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
    waitAsyncOperation()
    val encodedActorRef: String = serializeActorRef(DEFAULT_ACTOR_REF)
    val map = Map(RegisterSocialIDAPI.SOCIAL_ID -> DEFAULT_SOCIAL_ID,
      RegisterSocialIDAPI.SOCIAL_ACTOR -> encodedActorRef)
    webClient.callRemoteAPI(RegisterSocialIDAPI, Some(map))
    waitAsyncOperation()
    waitAsyncOperation()
    webClient.callRemoteAPI(GetAllOnlinePlayersAPI, None)
    waitAsyncOperation()
    waitAsyncOperation()
    assert(controller.playerList.nonEmpty)
  }

  test("Remove new ID to social Actor") {
    waitAsyncOperation()
    val encodedActorRef: String = serializeActorRef(DEFAULT_ACTOR_REF)
    val addingmap = Map(RegisterSocialIDAPI.SOCIAL_ID -> DEFAULT_SOCIAL_ID,
      RegisterSocialIDAPI.SOCIAL_ACTOR -> encodedActorRef)
    webClient.callRemoteAPI(RegisterSocialIDAPI, Some(addingmap))
    waitAsyncOperation()
    val removemap = Map(UnregisterSocialIDAPI.SOCIAL_ID -> DEFAULT_SOCIAL_ID)
    webClient.callRemoteAPI(UnregisterSocialIDAPI, Some(removemap))
    waitAsyncOperation()
    webClient.callRemoteAPI(GetAllOnlinePlayersAPI, None)
    waitAsyncOperation()
    assert(controller.playerList.isEmpty)
  }


  class MockSocialController extends SocialController {

    override var currentActorRef: ActorRef = _
    var bodyResponse: String = _
    var playerList: SocialMap = _

    override def notifyCallResultToGUI(message: Option[String]): Unit = bodyResponse = message.get

    override def setAndDisplayOnlinePlayerList(playerList: SocialMap): Unit = this.playerList = playerList

    override def createActor(actorID: String, actorSystem: ActorSystem): Unit = ???

    override def updateGUI(message: ActorMessage): Unit = ???

    override def displayFriendRequest(requestSender: PlayerID): Unit = ???

    override def notifyErrorToGUI(throwable: Throwable): Unit = ???

    override def displayPartyInvite(requestSender: PlayerID, role: PartyRole): Unit = ???

    override def displayResponse(message: String): Unit = ???

    override def registerNewFriend(friendId: PlayerID): Unit = ???

    override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit = ???

    override def executeFoundGameCall(paramMap: Map[String, String]): Unit = ???
  }


}


