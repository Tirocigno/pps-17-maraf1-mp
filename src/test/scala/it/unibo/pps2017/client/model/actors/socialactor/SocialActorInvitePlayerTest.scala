
package it.unibo.pps2017.client.model.actors.socialactor

import akka.actor.ActorRef
import akka.testkit.{ImplicitSender, TestKit}
import it.unibo.pps2017.client.model.actors.socialactor.controllers.{NegativeInviteController, PositiveInviteController, SenderSocialActorInviteController, SocialActorRequestController}
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.TellInvitePlayerRequestMessage
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, Partner}
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference
import it.unibo.pps2017.discovery.actors.RegistryActor.OnlinePlayerListMessage
import org.scalatest.{BeforeAndAfterEach, FunSuiteLike}

class SocialActorInvitePlayerTest extends TestKit(AkkaClusterUtils.startJoiningActorSystem("0", "127.0.0.1"))
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

  override def beforeEach() {
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
    setupOnlinePlayersList(leaderRef.playerRef)
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Partner)
    awaitForMessageExchange()
    assert(leaderController.partner.isDefined &
      leaderController.partner.get.equals(partnerRef.playerID))
  }

  test("Failing partner request: Refused for user will") {
    startNegativePartner()
    setupOnlinePlayersList(leaderRef.playerRef)
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Partner)
    awaitForMessageExchange()
    assert(leaderController.partner.isEmpty &&
      leaderController.socialResponse.equals(NegativeResponse))
  }

  test("Failing partner request: selected Partner already in formation as Partner") {
    startPositivePartner()
    setupOnlinePlayersList(leaderRef.playerRef)
    setupOnlinePlayersList(foeRef.playerRef)
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Partner)
    awaitForMessageExchange()
    foeRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Foe)
    awaitForMessageExchange()
    assert(leaderController.partner.isDefined &&
      leaderController.socialResponse.equals(PositiveResponse) &&
      leaderController.partner.get.equals(PARTNER_ID) &&
      foeController.socialResponse.equals(NegativeResponse) &&
      foeController.foe.isEmpty)
  }

  test("Failing partner request: Partner already in formation as Foe") {
    startPositivePartner()
    setupOnlinePlayersList(leaderRef.playerRef)
    setupOnlinePlayersList(foeRef.playerRef)
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Foe)
    awaitForMessageExchange()
    foeRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Partner)
    awaitForMessageExchange()
    assert(leaderController.foe.isDefined &&
      leaderController.socialResponse.equals(PositiveResponse) &&
      leaderController.foe.get.equals(PARTNER_ID) &&
      foeController.socialResponse.equals(NegativeResponse) &&
      foeController.partner.isEmpty)
  }

  test("Full party creation") {
    startPositivePartner()
    setupOnlinePlayersList(leaderRef.playerRef)
    setupOnlinePlayersList(foeRef.playerRef)
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(partnerRef.playerID, Partner)
    awaitForMessageExchange()
    foeRef.playerRef ! TellInvitePlayerRequestMessage(foePartnerRef.playerID, Partner)
    awaitForMessageExchange()
    leaderRef.playerRef ! TellInvitePlayerRequestMessage(foeRef.playerID, Foe)
    awaitForMessageExchange()
    assert(
      leaderController.socialResponse.equals(PositiveResponse) &&
        leaderController.partner.get.equals(PARTNER_ID) &&
        leaderController.foe.get.equals(FOE_ID) &&
        leaderController.foePartner.get.equals(FOE_PARTNER_ID))
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

  private def setupOnlinePlayersList(actorRef: ActorRef): Unit =
    actorRef !
      OnlinePlayerListMessage(onlinePlayerList.map(player => (player.playerID, player.playerRef)).toMap)

  private def awaitForMessageExchange(): Unit = Thread.sleep(DEFAULT_WAIT)


}
