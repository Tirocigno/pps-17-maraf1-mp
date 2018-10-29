
package it.unibo.pps2017.client.model.actors.socialactor

import akka.testkit.{ImplicitSender, TestKit}
import it.unibo.pps2017.client.model.actors.socialactor.controllers.{NegativeInviteController, PositiveInviteController, SenderSocialActorInviteController, SocialActorRequestController}
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.TellInvitePlayerRequestMessage
import it.unibo.pps2017.commons.remote.akka.AkkaTestUtils
import it.unibo.pps2017.commons.remote.social.PartyRole.Partner
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference
import org.scalatest.{BeforeAndAfterEach, FunSuiteLike}

class SocialActorInvitePlayerTest extends TestKit(AkkaTestUtils.generateTestActorSystem())
  with ImplicitSender with FunSuiteLike with BeforeAndAfterEach {

  val LEADER_ID = "Morgoth"
  val PARTNER_ID = "Sauron"
  val FOE_ID = "Gandlaf"
  val FOE_PARTNER_ID = "Legolas"
  val DEFAULT_WAIT: Int = 1000
  var leaderController: SenderSocialActorInviteController = _
  var partnerController: SocialActorRequestController = _
  var foeController: SenderSocialActorInviteController = _
  var foePartnerController: SocialActorRequestController = _
  var leaderRef: PlayerReference = _
  var partnerRef: PlayerReference = _
  var foeRef: PlayerReference = _
  var foePartnerRef: PlayerReference = _
  var onlinePlayerList: List[PlayerReference] = _

  beforeEach() {
    leaderController = new SenderSocialActorInviteController()
    leaderRef = PlayerReference(LEADER_ID, SocialActor(system, leaderController, LEADER_ID))
    leaderController.setCurrentActorRef(leaderRef.playerRef)

    foeController = new SenderSocialActorInviteController()
    foeRef = PlayerReference(FOE_ID, SocialActor(system, foeController, FOE_ID))
    foeController.setCurrentActorRef(foeRef.playerRef)

    foePartnerController = new PositiveInviteController()
    foePartnerRef = PlayerReference(FOE_PARTNER_ID, SocialActor(system, foePartnerController, FOE_PARTNER_ID))
    foePartnerController.setCurrentActorRef(foePartnerRef.playerRef)

    onlinePlayerList = List(leaderRef, foePartnerRef, foeRef)
  }

  test("Successful partner request") {
    startPositivePartner()
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Partner)
    awaitForMessageExchange()
    assert(leaderController.partner.isDefined &
      leaderController.partner.get.equals(partnerRef.playerID))
  }

  private def startPositivePartner(): Unit = {
    partnerController = new PositiveInviteController()
    partnerRef = PlayerReference(PARTNER_ID, SocialActor(system, partnerController, PARTNER_ID))
    partnerController.setCurrentActorRef(partnerRef.playerRef)
    onlinePlayerList = partnerRef :: onlinePlayerList
  }

  private def startNegativePartner(): Unit = {
    partnerController = new NegativeInviteController()
    partnerRef = PlayerReference(PARTNER_ID, SocialActor(system, partnerController, PARTNER_ID))
    partnerController.setCurrentActorRef(partnerRef.playerRef)
    onlinePlayerList = partnerRef :: onlinePlayerList
  }

  private def awaitForMessageExchange(): Unit = Thread.sleep(DEFAULT_WAIT)


}
