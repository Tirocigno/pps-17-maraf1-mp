
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.RequestClass
import it.unibo.pps2017.commons.remote.exceptions.AlreadyProcessingARequestException
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
    */
  def registerFriendshipRequest(requestClass: RequestClass, sender: PlayerReference): Unit

  def registerInviteRequest(requestClass: RequestClass, sender: PlayerReference, role: PartyRole)

  /**
    * Generate a response message and send it to the sender.
    *
    * @param socialResponse the response sent by the user.
    */
  def respondToRequest(socialResponse: SocialResponse): Unit
}

object RequestHandler {

  private class RequestHandlerImpl(val currentActorRef: ActorRef, val currentParty: SocialParty) extends RequestHandler {
    var currentRequest: Option[RequestClass] = None
    var senderPlayer: Option[PlayerReference] = None
    var inviteRole: Option[PartyRole] = None

    override def isAlreadyProcessingARequest: Boolean = currentRequest.isDefined

    override def registerFriendshipRequest(requestClass: RequestClass, sender: PlayerReference): Unit =
      checkRequestAndExecute(requestClass, sender)(friendshipHandler)

    override def respondToRequest(socialResponse: SocialResponse): Unit = ???

    override def registerInviteRequest(requestClass: RequestClass, sender: PlayerReference, role: PartyRole): Unit =
      checkRequestAndExecute(requestClass, sender)(inviteHandler(role))

    /**
      * Reset internal request fields after each response.
      */
    def resetRequestHandler(): Unit = {
      currentRequest = None
    senderPlayer = None
      inviteRole = None
    }

    /**
      * Check if a request is already served, if not register a new one
      *
      * @param requestClass    the request to register
      * @param playerReference the sender of the request
      * @param requestHandler  a function containing the operation to register that request.
      */
    private def checkRequestAndExecute(requestClass: RequestClass, playerReference: PlayerReference)
                                      (requestHandler: (RequestClass, PlayerReference) => Unit): Unit =
      currentRequest match {
        case Some(_) => throw new AlreadyProcessingARequestException()
        case None => requestHandler(requestClass, playerReference)
      }

    /**
      * Handler for a friendship request.
      *
      * @return an handler for a friendship request.
      */
    private def friendshipHandler: (RequestClass, PlayerReference) => Unit = (requestClass, sender) =>
      setRequestAndPlayerReferences(requestClass, sender)

    private def setRequestAndPlayerReferences(requestClass: RequestClass, playerReference: PlayerReference) = {
      currentRequest = Some(requestClass)
      senderPlayer = Some(playerReference)
    }

    /**
      * Set the current role, request and player, then if the member was already in a party, send a negative respond
      * to sender.
      *
      * @param role         the role inside the request.
      * @param requestClass the request to handle.
      * @param sender       the sender of the request.
      */
    private def inviteHandler(role: PartyRole)(requestClass: RequestClass, sender: PlayerReference): Unit = {
      inviteRole = Some(role)
      setRequestAndPlayerReferences(requestClass, sender)
      if (!currentParty.isLeader) {
        respondToRequest(SocialResponse.NegativeResponse)
      }
    }
  }

}
