
package it.unibo.pps2017.client.model.actors.socialactor.socialstructures

import akka.actor.ActorRef
import it.unibo.pps2017.client.model.actors.socialactor.socialmessages.SocialMessages.RequestClass
import it.unibo.pps2017.commons.remote.exceptions.AlreadyProcessingARequestException
import it.unibo.pps2017.commons.remote.social.SocialResponse
import it.unibo.pps2017.commons.remote.social.SocialUtils.PlayerReference

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
    * Register a new request inside the system.
    */
  def registerRequest(requestClass: RequestClass, sender: PlayerReference): Unit

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

    override def isAlreadyProcessingARequest: Boolean = currentRequest.isDefined

    override def registerRequest(requestClass: RequestClass, sender: PlayerReference): Unit = currentRequest match {
      case Some(_) => throw new AlreadyProcessingARequestException()
      case None => currentRequest = Some(requestClass); senderPlayer = Some(sender)
    }

    override def respondToRequest(socialResponse: SocialResponse): Unit = ???

    def resetRequestHandler: Unit = currentRequest = None;
    senderPlayer = None
  }

}
