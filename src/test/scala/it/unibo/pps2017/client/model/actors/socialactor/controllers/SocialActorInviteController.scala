
package it.unibo.pps2017.client.model.actors.socialactor.controllers

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{InvitePlayerResponseMessage, TellInvitePlayerResponseMessage}
import it.unibo.pps2017.commons.remote.social.PartyPlayer.{FoePlayer, PartnerPlayer}
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.FriendList

/**
  * Abstract class to provide a superclass for all possible controller instantiation.
  */
abstract class SocialActorInviteController extends SocialActorRequestController

class PositiveInviteController extends SocialActorInviteController {
  override def updateGUI(message: ActorMessage): Unit =
    currentActorRef ! TellInvitePlayerResponseMessage(PositiveResponse)
}

class NegativeInviteController extends SocialActorInviteController {
  override def updateGUI(message: ActorMessage): Unit =
    currentActorRef ! TellInvitePlayerResponseMessage(NegativeResponse)
}

class SenderSocialActorInviteController() extends SocialActorInviteController {
  var socialResponse: SocialResponse = _
  var partner: Option[String] = None
  var foe: Option[String] = None
  var foePartner: Option[String] = None

  override def updateOnlinePlayerList(playerRefList: FriendList): Unit = {}

  override def updateGUI(message: ActorMessage): Unit = message match {
    case InvitePlayerResponseMessage(response, myRole, partnerRole) => socialResponse = response
      myRole match {
        case Some(role) => role match {
          case PartnerPlayer(playerReference) => partner = Some(playerReference.playerID)
          case FoePlayer(playerReference) => partner = Some(playerReference.playerID)
        }
        case None =>
      }
  }

}