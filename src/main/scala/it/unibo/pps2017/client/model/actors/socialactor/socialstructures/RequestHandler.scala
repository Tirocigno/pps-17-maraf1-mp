
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages._
import it.unibo.pps2017.commons.remote.exceptions.AlreadyProcessingARequestException
import it.unibo.pps2017.commons.remote.social.PartyPlayer.{FoePlayer, PartnerPlayer}
import it.unibo.pps2017.commons.remote.social.PartyRole.{Foe, Partner}
import it.unibo.pps2017.commons.remote.social.SocialResponse.{NegativeResponse, PositiveResponse}
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference
import it.unibo.pps2017.commons.remote.social.{PartyRole, SocialResponse}

/**
  * A class for register the status of a received request.
  * This class will keep track of the kind of request and the sender context.
  * Only one request at each time must be handled.
  */
trait RequestHandler {

  /**
    * Check if the Handler is already processing a request.
    *
    * @return true if a request is handled, false otherwise.
    */
  def isAlreadyProcessingARequest: Boolean

  /**
    * Register a new friendship request inside the system.
    *
    * @param requestMessage the message containing the request.
    */
  def registerRequest(requestMessage: RequestMessage): Unit

  /**
    * Generate a response message and send it to the sender.
    *
    * @param socialResponse the response sent by the user.
    */
  def respondToRequest(socialResponse: SocialResponse): Unit
}

object RequestHandler {

  def apply(currentPlayerRef: PlayerReference, currentParty: SocialParty): RequestHandler =
    new RequestHandlerImpl(currentPlayerRef, currentParty)

  private class RequestHandlerImpl(val currentPlayerRef: PlayerReference, val currentParty: SocialParty) extends RequestHandler {
    var currentMessage: Option[RequestMessage] = None


    override def isAlreadyProcessingARequest: Boolean = currentMessage.isDefined

    override def registerRequest(requestMessage: RequestMessage): Unit = requestMessage match {
      case AddFriendRequestMessage(_) => checkRequestAndExecute(requestMessage)(friendshipHandler)
      case InvitePlayerRequestMessage(_, _) => checkRequestAndExecute(requestMessage)(inviteHandler)
    }

    override def respondToRequest(socialResponse: SocialResponse): Unit =
      checkMessageAndElaborateResponse(socialResponse)


    /**
      * Check if a request is already served, if not register a new one
      *
      * @param message        the request message.
      * @param requestHandler a function containing the operation to register that request.
      */
    private def checkRequestAndExecute(message: RequestMessage)
                                      (requestHandler: RequestMessage => Unit): Unit =
      currentMessage match {
        case Some(_) => throw new AlreadyProcessingARequestException()
        case None => requestHandler(message)
      }


    /**
      * Handler for a friendship request.
      *
      * @return an handler for a friendship request.
      */
    private def friendshipHandler: RequestMessage => Unit = requestMessage =>
      setRequestAndPlayerReferences(requestMessage)

    private def setRequestAndPlayerReferences(requestMessage: RequestMessage): Unit =
      currentMessage = Some(requestMessage)

    /**
      * Set the current role, request and player, then if the member was already in a party, send a negative respond
      * to sender.
      *
      * @param requestMessage the request message.
      */
    private def inviteHandler(requestMessage: RequestMessage): Unit = {
      setRequestAndPlayerReferences(requestMessage)
      if (!currentParty.isLeader) {
        respondToRequest(SocialResponse.NegativeResponse)
      }
    }

    /**
      * Check if a message is present and elaborate its message response, otherwise throws a NoSuchElement exception.
      *
      * @param socialResponse the response sent by the user.
      */
    private def checkMessageAndElaborateResponse(socialResponse: SocialResponse): Unit = currentMessage match {
      case Some(message) => elaborateResponse(message, socialResponse)
      case None => throw new NoSuchElementException()
    }

    /**
      * Elaborate a response based on the message and the response.
      *
      * @param socialMessage  the message to respond to.
      * @param socialResponse the response, could be positive or negative.
      */
    private def elaborateResponse(socialMessage: RequestMessage, socialResponse: SocialResponse): Unit =
      socialMessage match {
        case AddFriendRequestMessage(sender) => generateFriendResponse(socialResponse, sender); resetRequestHandler()
        case InvitePlayerRequestMessage(sender, role) => generateInviteResponse(socialResponse, sender, role); resetRequestHandler()
      }

    /**
      * Generate a response for a FriendRequest message and sends it to request sender,
      *
      * @param socialResponse the response, could be positive or negative.
      * @param sender         the sender of request.
      */
    private def generateFriendResponse(socialResponse: SocialResponse, sender: PlayerReference): Unit =
      sender.playerRef ! AddFriendResponseMessage(socialResponse)

    /**
      * Generate a response for a InviteRequest message and it sends back to sender.
      *
      * @param socialResponse  the response, could be positive or negative.
      * @param playerReference the sender references.
      * @param role            the role on which the request want the player to play as.
      */
    private def generateInviteResponse(socialResponse: SocialResponse,
                                       playerReference: PlayerReference, role: PartyRole): Unit = socialResponse match {
      case NegativeResponse => playerReference.playerRef ! InvitePlayerResponseMessage(socialResponse, None, None)
        resetRequestHandler()
      case PositiveResponse => playerReference.playerRef ! generateInviteResponseMessage(socialResponse, role)
        resetRequestHandler()
    }

    /**
      * Generate the response message for a invite request.
      *
      * @param socialResponse the response, could be positive or negative.
      * @param role           role the role on which the request want the player to play as.
      * @return an InvitePlayerResponse message.
      */
    private def generateInviteResponseMessage(socialResponse: SocialResponse, role: PartyRole): SocialMessage =
      role match {
        case Partner => InvitePlayerResponseMessage(socialResponse, Some(PartnerPlayer(currentPlayerRef)), None)
        case Foe => InvitePlayerResponseMessage(socialResponse,
          Some(FoePlayer(currentPlayerRef)), currentParty.getPartner)
        case _ => throw new IllegalArgumentException()
      }


    /**
      * Reset internal request fields after each response.
      */
    def resetRequestHandler(): Unit = currentMessage = None
  }

}
