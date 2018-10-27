
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
    var inviteClass: Option[PartyRole] = None

    override def isAlreadyProcessingARequest: Boolean = currentRequest.isDefined

    override def registerFriendshipRequest(requestClass: RequestClass, sender: PlayerReference): Unit =
      checkRequestAndExecute(requestClass, sender)(friendshipHandler)

    private def checkRequestAndExecute(requestClass: RequestClass, playerReference: PlayerReference)
                                      (requestHandler: (RequestClass, PlayerReference) => Unit): Unit =
      currentRequest match {
        case Some(_) => throw new AlreadyProcessingARequestException()
        case None => requestHandler(requestClass, playerReference)
    }


    override def respondToRequest(socialResponse: SocialResponse): Unit = ???

    private def friendshipHandler: (RequestClass, PlayerReference) => Unit = (requestClass, sender) => {
      currentRequest = Some(requestClass)
      senderPlayer = Some(sender)
    }

    override def registerInviteRequest(requestClass: RequestClass, sender: PlayerReference, role: PartyRole): Unit = ???

    def resetRequestHandler(): Unit = currentRequest = None
    senderPlayer = None
  }

}
