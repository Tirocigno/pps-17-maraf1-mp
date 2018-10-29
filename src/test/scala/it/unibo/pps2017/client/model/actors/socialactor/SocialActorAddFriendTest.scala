
package it.unibo.pps2017.client.model.actors.socialactor

import akka.actor.ActorRef
import akka.testkit.{ImplicitSender, TestKit}
import it.unibo.pps2017.client.model.actors.socialactor.controllers.{NegativeSocialActorAddFriendController, PositiveSocialActorAddFriendController, SenderSocialActorAddFriendController, SocialActorRequestController}
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{AddFriendRequestMessage, AddFriendResponseMessage, SetOnlinePlayersMapMessage, TellAddFriendRequestMessage}
import it.unibo.pps2017.commons.remote.akka.AkkaTestUtils
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, PlayerReference}
import org.junit.runner.RunWith
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SocialActorAddFriendTest()
  extends TestKit(AkkaTestUtils.generateTestActorSystem()) with ImplicitSender with FunSuiteLike {

  val PLAYER_ID: PlayerID = SocialActorRequestController.MOCK_PLAYER_ID
  val SENDER_ID: PlayerID = "SENDER"
  val DEFAULT_WAIT: Int = 1000
  var sender: PlayerReference = PlayerReference(SENDER_ID, testActor)
  var controller: SocialActorRequestController = _
  var socialRef: ActorRef = _

  test("Adding a friend with a positive response") {
    controller = new PositiveSocialActorAddFriendController()
    socialRef = SocialActor(system, controller, PLAYER_ID)
    controller.setCurrentActorRef(socialRef)
    socialRef ! AddFriendRequestMessage(sender)
    val msg = expectMsgType[AddFriendResponseMessage]
    assert(msg.socialResponse.message.equals(PositiveResponse.message) &&
      msg.senderID.equals(PLAYER_ID))
  }

  test("Adding a friend with a negative") {
    controller = new NegativeSocialActorAddFriendController()
    socialRef = SocialActor(system, controller, PLAYER_ID)
    controller.setCurrentActorRef(socialRef)
    socialRef ! AddFriendRequestMessage(sender)
    val msg = expectMsgType[AddFriendResponseMessage]
    assert(msg.socialResponse.message.equals(NegativeResponse.message) &&
      msg.senderID.equals(PLAYER_ID))
  }

  test("Adding a new friend from an actor with positive response") {
    addingNewFriendFromActorTest(PositiveResponse)
  }

  test("Adding a new friend from an actor with negative response") {
    addingNewFriendFromActorTest(NegativeResponse)
  }

  private def addingNewFriendFromActorTest(expectedResponse: SocialResponse) = {
    expectedResponse match {
      case PositiveResponse => controller = new PositiveSocialActorAddFriendController()
      case NegativeResponse => controller = new NegativeSocialActorAddFriendController()
    }
    socialRef = SocialActor(system, controller, PLAYER_ID)
    controller.setCurrentActorRef(socialRef)
    val senderController = new SenderSocialActorAddFriendController()
    val senderActor = SocialActor(system, senderController, SENDER_ID)
    senderController.setCurrentActorRef(senderActor)
    senderActor ! SetOnlinePlayersMapMessage(List(PlayerReference(PLAYER_ID, socialRef)))
    senderActor ! TellAddFriendRequestMessage(PLAYER_ID)
    waitMessageExchange()
    assert(senderController.playerID.equals(PLAYER_ID) && senderController.response.equals(expectedResponse))
  }

  private def waitMessageExchange(): Unit = {
    Thread.sleep(DEFAULT_WAIT)
  }


}
