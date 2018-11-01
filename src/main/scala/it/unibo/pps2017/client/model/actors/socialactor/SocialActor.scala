
package it.unibo.pps2017.client.model.actors.socialactor

import java.util.NoSuchElementException

import akka.actor.{ActorRef, ActorSystem, Props, Stash}
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.actors.ModelActor
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.client.model.actors.socialactor.socialstructures.{RequestHandler, SocialParty, SocialPlayersMap}
import it.unibo.pps2017.commons.remote.social.PartyPlayer.{FoePlayer, PartnerPlayer}
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, FoePartner, Partner}
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, PlayerReference}
import it.unibo.pps2017.commons.remote.social.{PartyPlayer, PartyRole, SocialResponse}

/**
  * The socialActor will be responsable of all the function in which real time
  * connection is necessary, such as sending and receiving friendship and
  * challenge requests.
  */

trait SocialActor extends ModelActor {
  override val controller: SocialController
}

object SocialActor {
  val FRIEND_MESSAGE_TO_DISPLAY = "RECEIVED FRIEND REQUEST FROM "
  val INVITE_MESSAGE_TO_DISPLAY = " WANTS YOU TO PLAY AS HIS "
  val ACCEPTED_REQUEST_TO_DISPLAY = "REQUEST ACCEPTED"
  val REFUSED_REQUEST_TO_DISPLAY = "REQUEST REFUSED"
  def apply(system: ActorSystem, socialController: SocialController, username: String): ActorRef =
    system.actorOf(Props(new SocialActorImpl(socialController, username)))

  private class SocialActorImpl(override val controller: SocialController,
                                override val username: String) extends SocialActor with Stash {

    val currentContext = PlayerReference(username, self)
    val socialParty: SocialParty = SocialParty(currentContext)
    val socialPlayersMap: SocialPlayersMap = SocialPlayersMap(currentContext.playerID)
    val requestHandler: RequestHandler = RequestHandler(currentContext, socialParty)

    override def receive: Receive = {
      case SetOnlinePlayersMapMessage(players) => socialPlayersMap.setOnlinePlayerList(players)
        controller.updateOnlinePlayerList(socialPlayersMap.getAllOnlineStrangers)
      case SetFriendsList(friendsList) => socialPlayersMap.setFriendsList(friendsList)
        controller.updateOnlineFriendsList(socialPlayersMap.getAllOnlineFriends)
      case TellAddFriendRequestMessage(playerID) => tellAddFriendRequestHandler(playerID)
      case message: AddFriendRequestMessage => stashOrElse(message, message.sender, addFriendRequestHandler)
      case TellAddFriendResponseMessage(response, _) => tellAddFriendResponseHandler(response)
      case message: AddFriendResponseMessage => addFriendResponseHandler(message.socialResponse, message.senderID)
        controller.updateGUI(message)
      case TellInvitePlayerRequestMessage(player, role) => tellInvitePlayerRequestMessage(player, role)
      case message: InvitePlayerRequestMessage => stashOrElse(message)
      case TellInvitePlayerResponseMessage(response) => tellInvitePlayerResponseHandler(response)
      case message: InvitePlayerResponseMessage =>
        invitePlayerResponseHandler(message.socialResponse, message.myRole, message.partnerRole)
        controller.updateGUI(message)
      case NotifyGameIDMessage(gameID) => if (socialParty.isLeader) {
        socialParty.notifyGameIDToAllPlayers(gameID)
      }
      case GameIDMessage(gameID) => controller.notifyGameController(gameID)
      case GetPartyAndStartGameMessage => buildStartGameRequest()
      case ResetParty => socialParty.resetParty()
      case UnstashAllMessages => unstashAll()

    }

    private def stashOrElse(message: RequestMessage): Unit = {
      if (requestHandler.isAlreadyProcessingARequest) {
        stash()
      } else {
        requestHandler.registerRequest(message)
        if (requestHandler.isAlreadyProcessingARequest) {
          controller.updateGUI(message)
        }
      }
    }


    private def stashOrElse[A](message: RequestMessage, arg: A, elseStrategy: A => Unit): Unit = {
      if (requestHandler.isAlreadyProcessingARequest) {
        stash()
      } else {
        requestHandler.registerRequest(message)
        controller.updateGUI(message)
        elseStrategy(arg)
      }
    }

    private def tellAddFriendRequestHandler(friendID: PlayerID): Unit = {
      try {
          socialPlayersMap.getPlayerID(friendID) ! AddFriendRequestMessage(currentContext)
      } catch {
        case _: NoSuchElementException => controller notifyErrorToGUI _
      }
    }

    private def addFriendRequestHandler(sender: PlayerReference): Unit = {
      socialPlayersMap.registerUser(sender.playerID, sender.playerRef)
    }

    private def tellAddFriendResponseHandler(response: SocialResponse): Unit = {
      requestHandler.respondToRequest(response)
      unstashAll()
    }

    private def addFriendResponseHandler(response: SocialResponse, playerID: PlayerID): Unit = response match {
      case PositiveResponse => controller.registerNewFriend(playerID)
        socialPlayersMap.updateFriendList(playerID)
      case _ =>
    }

    private def tellInvitePlayerRequestMessage(playerID: PlayerID, role: PartyRole): Unit = {
      try {
        socialPlayersMap.getPlayerID(playerID) ! InvitePlayerRequestMessage(currentContext, role)
      } catch {
        case _: NoSuchElementException => controller notifyErrorToGUI _
      }
    }

    private def tellInvitePlayerResponseHandler(socialResponse: SocialResponse): Unit = {
      requestHandler.respondToRequest(socialResponse)
      unstashAll()
    }

    private def invitePlayerResponseHandler(socialResponse: SocialResponse, myRole: Option[PartyPlayer],
                                            partnerRole: Option[PlayerReference]): Unit = socialResponse match {
      case NegativeResponse =>
      case PositiveResponse => updateParty(myRole.get, partnerRole)
        controller.updateParty(socialParty.getAllPlayers.map(r => (r._1, r._2.playerID)))
    }

    private def updateParty(player: PartyPlayer, partner: Option[PlayerReference]): Unit = player match {
      case PartnerPlayer(reference) => socialParty.setPlayerInParty(Partner, reference)
      case FoePlayer(reference) =>
        socialParty.setPlayerInParty(Foe, reference)
        if (partner.isDefined) {
          socialParty.setPlayerInParty(FoePartner, partner.get)
        }
    }

    private def buildStartGameRequest(): Unit = {
      val parameterMap: Map[String, String] =
        socialParty.getAllPlayers.map(tuple => (tuple._1.asRestParameter, tuple._2.playerID))
      controller.executeFoundGameCall(parameterMap)
    }

  }
}
