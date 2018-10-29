
package it.unibo.pps2017.client.model.actors.socialactor

import akka.actor.ActorRef
import akka.testkit.{ImplicitSender, TestKit}
import it.unibo.pps2017.client.model.actors.socialactor.controllers.{NegativeSocialActorAddFriendController, PositiveSocialActorAddFriendController, SenderSocialActorAddFriendController, SocialActorAddFriendController}
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{AddFriendRequestMessage, AddFriendResponseMessage, SetOnlinePlayersMapMessage, TellAddFriendRequestMessage}
import it.unibo.pps2017.commons.remote.akka.AkkaTestUtils
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, PlayerReference}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuiteLike}

@RunWith(classOf[JUnitRunner])
class SocialActorAddFriendTest()
  extends TestKit(AkkaTestUtils.generateTestActorSystem()) with ImplicitSender with FunSuiteLike with BeforeAndAfterEach {

  val PLAYER_ID: PlayerID = SocialActorAddFriendController.MOCK_PLAYER_ID
  val SENDER_ID: PlayerID = "SENDER"
  var sender: PlayerReference = PlayerReference(SENDER_ID, testActor)
  var controller: SocialActorAddFriendController = _
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

  test("Adding a new friend from an actor") {
    controller = new PositiveSocialActorAddFriendController()
    socialRef = SocialActor(system, controller, PLAYER_ID)
    controller.setCurrentActorRef(socialRef)
    val senderController = new SenderSocialActorAddFriendController()
    val senderActor = SocialActor(system, senderController, SENDER_ID)
    senderController.setCurrentActorRef(senderActor)
    senderActor ! SetOnlinePlayersMapMessage(List(PlayerReference(PLAYER_ID, socialRef)))
    senderActor ! TellAddFriendRequestMessage(PLAYER_ID)
    Thread.sleep(2000)
    assert(senderController.playerID.equals(PLAYER_ID) && senderController.response.equals(PositiveResponse))

  }


}
