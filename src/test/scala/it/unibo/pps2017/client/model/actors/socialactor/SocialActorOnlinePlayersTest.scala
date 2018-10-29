
package it.unibo.pps2017.client.model.actors.socialactor

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.socialactor.controllers.SocialListController
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{SetFriendsList, SetOnlinePlayersMapMessage}
import it.unibo.pps2017.commons.remote.akka.AkkaTestUtils
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class SocialActorOnlinePlayersTest extends FunSuite with BeforeAndAfterEach {

  val PLAYER_1 = PlayerReference("Jacopo", ActorRef.noSender)
  val PLAYER_2 = PlayerReference("Federico", ActorRef.noSender)
  val PLAYER_3 = PlayerReference("DGjulio", ActorRef.noSender)
  val PLAYER_4 = PlayerReference("Nicholas", ActorRef.noSender)
  val OFFLINE_FRIEND = PlayerReference("SantaClause", ActorRef.noSender)
  val PLAYER_ID = PlayerReference("WitchKingOfAlmar", ActorRef.noSender)
  val onlinePlayers = List(PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4)
  val myFriends = List(PLAYER_3.playerID, OFFLINE_FRIEND.playerID)
  val myOnlineFriends = List(PLAYER_3.playerID)
  val actorSystem = AkkaTestUtils.generateTestActorSystem()
  var controller: SocialListController = _
  var actorRef: ActorRef = _


  override def beforeEach() {
    controller = new SocialListController()
    actorRef = SocialActor(actorSystem, controller, PLAYER_ID.playerID)
  }

  test("Set online players list") {
    actorRef ! SetOnlinePlayersMapMessage(onlinePlayers)
    waitForAsyncOperation()
    assert(controller.onlinePlayerList.forall(onlinePlayers.map(_.playerID).contains))
  }

  test("Player's ID is not included inside the online players List") {
    val secondList = PLAYER_ID :: onlinePlayers
    actorRef ! SetOnlinePlayersMapMessage(secondList)
    waitForAsyncOperation()
    assert(controller.onlinePlayerList.forall(onlinePlayers.map(_.playerID).contains))
  }

  test("Online friend list status") {
    actorRef ! SetOnlinePlayersMapMessage(onlinePlayers)
    waitForAsyncOperation()
    actorRef ! SetFriendsList(myFriends)
    waitForAsyncOperation()
    assert(controller.friendList.forall(myOnlineFriends.contains))
  }

  private def waitForAsyncOperation(): Unit = Thread.sleep(1000)

}
