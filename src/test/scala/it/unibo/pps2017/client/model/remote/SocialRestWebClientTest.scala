
package it.unibo.pps2017.client.model.remote

import akka.actor.{ActorRef, ActorSystem}
import io.vertx.scala.core.Vertx
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.view.social.SocialGUIController
import it.unibo.pps2017.commons.remote.akka.AkkaTestUtils
import it.unibo.pps2017.commons.remote.game.MatchNature
import it.unibo.pps2017.commons.remote.rest.RestUtils.{ServerContext, serializeActorRef}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{FriendList, PlayerID, SocialMap}
import it.unibo.pps2017.commons.remote.social.{PartyRole, SocialResponse}
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

  test("Register new ID to socialcontroller Actor") {
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

  test("Remove new ID to socialcontroller Actor") {
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

    override def setOnlinePlayerList(playerList: SocialMap): Unit = this.playerList = playerList

    override def createActor(actorID: String, actorSystem: ActorSystem): Unit = {}

    override def updateGUI(message: ActorMessage): Unit = {}

    override def notifyErrorToGUI(throwable: Throwable): Unit = {}

    override def registerNewFriend(friendId: PlayerID): Unit = {}

    override def updateParty(currentPartyMap: Map[PartyRole, PlayerID]): Unit = {}

    override def executeFoundGameCall(paramMap: Map[String, String]): Unit = {}

    override def updateOnlineFriendsList(friendList: FriendList): Unit = {}

    override def updateOnlinePlayerList(friendList: FriendList): Unit = {}

    override def tellFriendShipMessage(playerID: PlayerID): Unit = {}

    override def tellInvitePlayerAsPartner(playerID: PlayerID): Unit = {}

    override def tellInvitePlayerAsFoe(playerID: PlayerID): Unit = {}

    override def startGame(matchNature: MatchNature.MatchNature): Unit = {}

    override def finishGame(): Unit = {}

    override def setCurrentGui(gui: SocialGUIController): Unit = {}

    override def notifyAllPlayersGameID(gameID: String): Unit = {}

    override def notifyGameController(gameID: String): Unit = {}

    override def shutDown(): Unit = {}

    override def notifyFriendMessageResponse(socialResponse: SocialResponse): Unit = {}

    override def notifyInviteMessageResponse(socialResponse: SocialResponse): Unit = {}

    override def getSocialGUIController: SocialGUIController = new it.unibo.pps2017.core.gui.SocialGUIController()

    override def setScoreInsideGUI(scores: Int): Unit = {}
  }


}


