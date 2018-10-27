
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.{AddFriendRequestMessage, InvitePlayerRequestMessage, RequestMessage}
import it.unibo.pps2017.commons.remote.exceptions.AlreadyProcessingARequestException
import it.unibo.pps2017.commons.remote.social.SocialResponse

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

  private class RequestHandlerImpl(val currentActorRef: ActorRef, val currentParty: SocialParty) extends RequestHandler {
    var currentMessage: Option[RequestMessage] = None

    override def isAlreadyProcessingARequest: Boolean = currentMessage.isDefined

    override def registerRequest(requestMessage: RequestMessage): Unit = requestMessage match {
      case AddFriendRequestMessage(_) => checkRequestAndExecute(requestMessage)(friendshipHandler)
      case InvitePlayerRequestMessage(_, _) => checkRequestAndExecute(requestMessage)(inviteHandler)
    }

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

    override def respondToRequest(socialResponse: SocialResponse): Unit = ???

    /**
      * Reset internal request fields after each response.
      */
    def resetRequestHandler(): Unit = currentMessage = None
  }

}
