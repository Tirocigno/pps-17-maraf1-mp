
package it.unibo.pps2017.client.model.actors.socialactor

import java.util.NoSuchElementException

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props, Stash}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import it.unibo.pps2017.client.controller.socialcontroller.SocialController
import it.unibo.pps2017.client.model.actors.ModelActor
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.client.model.actors.socialactor.socialstructures.{RequestHandler, SocialParty, SocialPlayersMap}
import it.unibo.pps2017.commons.remote.social.PartyPlayer.{FoePlayer, PartnerPlayer}
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, FoePartner, Partner}
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, PlayerReference, SocialMap}
import it.unibo.pps2017.commons.remote.social.{PartyPlayer, PartyRole, SocialResponse}
import it.unibo.pps2017.discovery.actors.RegistryActor
import it.unibo.pps2017.discovery.actors.RegistryActor.{AddUserToRegisterMessage, HeartBeatMessage, OnlinePlayerListMessage, RemoveUserFromRegisterMessage}

/**
  * The socialActor will be responsible of all the function in which real time
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
    val requestHandler: RequestHandler = RequestHandler(currentContext, socialParty, socialPlayersMap, controller)
    val mediator: ActorRef = DistributedPubSub(context.system).mediator
    mediator ! Subscribe(RegistryActor.SOCIAL_CHANNEL, self)
    var remoteRegistryActor: Option[ActorRef] = None

    override def receive: Receive = {
      case SetFriendsList(friendsList) => socialPlayersMap.setFriendsList(friendsList)
        updatePlayersList()
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
      case HeartBeatMessage(sender) => heartBeatHandler(sender)
      case OnlinePlayerListMessage(map) => onlinePlayerListMessageHandler(map)
      case KillYourSelf => killYourSelfHandler()
      case SubscribeAck(_) => controller.startHeartBeatRequest()
    }

    /**
      * Handler for an heartbeat message.
      *
      * @param sender actorRef of the message sender.
      */
    private def heartBeatHandler(sender: ActorRef): Unit = remoteRegistryActor match {
      case Some(_) =>
      case None =>
        remoteRegistryActor = Some(sender)
        sender ! AddUserToRegisterMessage(currentContext.playerID, currentContext.playerRef)
    }

    /**
      * Handler for an OnlinePlayerListMessage message.
      *
      * @param socialMap a map containing the current online players usernames and actorRefs.
      */
    private def onlinePlayerListMessageHandler(socialMap: SocialMap): Unit = {
      val players = socialMap.map(entry => PlayerReference(entry._1, entry._2)).toList
      socialPlayersMap.setOnlinePlayerList(players)
      updatePlayersList()
    }

    /**
      * Handler for the KillYourSelfMessage message.
      */
    private def killYourSelfHandler(): Unit = {
      remoteRegistryActor match {
        case Some(actorRef) => actorRef ! RemoveUserFromRegisterMessage(currentContext.playerID)
        case None =>
      }
      self ! PoisonPill
    }

    /**
      * Check if a request is already processed, if it is, then stash the message, else register that request.
      *
      * @param message the message to process.
      */
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

    /**
      * Generic version of the stashOrElse function.
      *
      * @param message      the message to process.
      * @param arg          the argument to be passed to the handler
      * @param elseStrategy a strategy function to be applied if the message is not stashed.
      * @tparam A the generic type of the argument.
      */
    private def stashOrElse[A](message: RequestMessage, arg: A, elseStrategy: A => Unit): Unit = {
      if (requestHandler.isAlreadyProcessingARequest) {
        stash()
      } else {
        requestHandler.registerRequest(message)
        controller.updateGUI(message)
        elseStrategy(arg)
      }
    }

    /**
      * Handler for TellAddFriendRequest message.
      *
      * @param friendID the username of the player to contact.
      */
    private def tellAddFriendRequestHandler(friendID: PlayerID): Unit = {
      try {
          socialPlayersMap.getPlayerID(friendID) ! AddFriendRequestMessage(currentContext)
      } catch {
        case _: NoSuchElementException => controller notifyErrorToGUI _
      }
    }

    /**
      * Handler for AddFriendRequest message.
      *
      * @param sender message sender's username.
      */
    private def addFriendRequestHandler(sender: PlayerReference): Unit = {
      socialPlayersMap.registerUser(sender.playerID, sender.playerRef)
    }

    /**
      * Handler for TellAddFriendResponse message.
      *
      * @param response the response to send.
      */
    private def tellAddFriendResponseHandler(response: SocialResponse): Unit = {
      response match {
        case PositiveResponse =>
          updatePlayersList()
        case NegativeResponse =>
      }
      requestHandler.respondToRequest(response)
      unstashAll()
    }

    /**
      * Handler for AddFriendResponse message.
      *
      * @param response the response to send.
      * @param playerID the message sender's username.
      */
    private def addFriendResponseHandler(response: SocialResponse, playerID: PlayerID): Unit = response match {
      case PositiveResponse => controller.registerNewFriend(playerID)
        socialPlayersMap.updateFriendList(playerID)
        updatePlayersList()
      case _ =>
    }

    /**
      * Handler for TellInvitePlayerRequest message.
      *
      * @param playerID username of the player to contact.
      * @param role     the role on which the recipient will play.
      */
    private def tellInvitePlayerRequestMessage(playerID: PlayerID, role: PartyRole): Unit = {
      try {
        socialPlayersMap.getPlayerID(playerID) ! InvitePlayerRequestMessage(currentContext, role)
      } catch {
        case _: NoSuchElementException => controller notifyErrorToGUI _
      }
    }

    /**
      * Handler for TellInvitePlayerResponse message.
      *
      * @param socialResponse the response to send.
      */
    private def tellInvitePlayerResponseHandler(socialResponse: SocialResponse): Unit = {
      requestHandler.respondToRequest(socialResponse)
      unstashAll()
    }

    /**
      * Handler for InvitePlayerResponse message.
      *
      * @param socialResponse the response to send.
      * @param myRole         a PartyPlayer object, which contains the player id and the game role specified by the request.
      * @param partnerRole    the reference to the partner, if present.
      */
    private def invitePlayerResponseHandler(socialResponse: SocialResponse, myRole: PartyPlayer,
                                            partnerRole: Option[PlayerReference]): Unit = socialResponse match {
      case NegativeResponse =>
      case PositiveResponse => updateParty(myRole, partnerRole)
        controller.updateParty(socialParty.getAllPlayers.map(r => (r._1, r._2.playerID)))
    }

    /**
      * Update the internal party.
      *
      * @param player  the player, saved as a PartyPlayer, to register inside the party.
      * @param partner player's partner reference, if present.
      */
    private def updateParty(player: PartyPlayer, partner: Option[PlayerReference]): Unit = player match {
      case PartnerPlayer(reference) => socialParty.setPlayerInParty(Partner, reference)
      case FoePlayer(reference) =>
        socialParty.setPlayerInParty(Foe, reference)
        if (partner.isDefined) {
          socialParty.setPlayerInParty(FoePartner, partner.get)
        }
    }

    /**
      * Build a parameter map for start game request based on the current party state.
      */
    private def buildStartGameRequest(): Unit = {
      val parameterMap: Map[String, String] =
        socialParty.getAllPlayers.map(tuple => (tuple._1.asRestParameter, tuple._2.playerID))
      controller.executeFoundGameCall(parameterMap)
    }

    /**
      * Update online strangers and online friends lists inside the GUI.
      */
    private def updatePlayersList(): Unit = {
      controller.updateOnlinePlayerList(socialPlayersMap.getAllOnlineStrangers)
      controller.updateOnlineFriendsList(socialPlayersMap.getAllOnlineFriends)
    }

  }
}
