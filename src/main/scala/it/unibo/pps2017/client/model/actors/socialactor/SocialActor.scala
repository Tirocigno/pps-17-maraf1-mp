
package it.unibo.pps2017.client.model.actors.socialactor

import java.util.NoSuchElementException

import akka.actor.{ActorRef, ActorSystem, Props, Stash}
import it.unibo.pps2017.client.controller.SocialController
import it.unibo.pps2017.client.model.actors.ModelActor
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.client.model.actors.socialactor.socialstructures.{RequestHandler, SocialParty, SocialPlayersMap}
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.{PlayerID, PlayerReference}

/**
  * The socialActor will be responsable of all the function in which real time
  * connection is necessary, such as sending and receiving friendship and
  * challenge requests.
  */
//TODO IMPLEMENT THIS ACTOR.
trait SocialActor extends ModelActor {
  override val controller: SocialController
}

object SocialActor {
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
      case SetFriendsList(friendsList) => socialPlayersMap.setFriendsList(friendsList)
      case TellAddFriendRequestMessage(playerID) => tellAddFriendRequestHandler(playerID)
      case message: AddFriendRequestMessage => stashOrElse(message, message.sender, addFriendRequestHandler)
      case TellAddFriendResponseMessage(response, _) => tellAddFriendResponseHandler(response)
      case AddFriendResponseMessage(response, sender) => addFriendResponseHandler(response, sender)
    }

    private def stashOrElse[A](message: RequestMessage, arg: A, elseStrategy: A => Unit): Unit = {
      if (requestHandler.isAlreadyProcessingARequest) {
        stash()
      } else {
        requestHandler.registerRequest(message)
        elseStrategy(arg)
      }
    }

    private def tellAddFriendRequestHandler(friendID: PlayerID): Unit = {
      try {
        if (!requestHandler.isAlreadyProcessingARequest) {
          socialPlayersMap.getPlayerID(friendID) ! AddFriendRequestMessage(currentContext)
        }
      } catch {
        case _: NoSuchElementException => controller notifyErrorToGUI _
      }
    }

    private def addFriendRequestHandler(sender: PlayerReference): Unit = {
      socialPlayersMap.registerUser(sender.playerID, sender.playerRef)
      controller.displayFriendRequest(sender.playerID)
    }

    private def tellAddFriendResponseHandler(response: SocialResponse): Unit = {
      requestHandler.respondToRequest(response)
      unstashAll()
    }

    private def addFriendResponseHandler(response: SocialResponse, playerID: PlayerID): Unit = response match {
      case PositiveResponse => controller.displayResponse(playerID + PositiveResponse.message)
        controller.registerNewFriend(playerID)
        socialPlayersMap.updateFriendList(playerID)
      case NegativeResponse => controller.displayResponse(playerID + NegativeResponse.message)
    }


  }
}
