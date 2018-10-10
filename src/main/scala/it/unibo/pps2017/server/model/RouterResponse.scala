
package it.unibo.pps2017.server.model

import io.circe.generic.auto._
import io.circe.syntax._
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.ResponseStatus._


/**
  * This class complete a routing request.
  * Allows to manage the data, the status and the message that will be sent to the user.
  *
  * @param routingContext
  *   Vert.x routingContext, contain the request data.
  * @param status
  *   Response status code.
  * @param message
  *   Message to the user.
  *   Used only in case of error.
  */
case class RouterResponse(routingContext: RoutingContext,
                          var status: HeaderStatus = OK,
                          var message: Option[String] = None) {

  /**
    * Set the response status to error.
    *
    * @param code
    * Error code.
    * @param message
    * Error message, is optional but can help the client to understand the error.
    * @return
    * This RouterResponse object.
    */
  def setError(code: HeaderStatus = ResponseException, message: Option[String] = None): RouterResponse = {
    status = code
    this.message = message

    this
  }

  /**
    * Set the response status to ResponseException.
    *
    * @param message
    * Error message, is optional but can help the client to understand the error.
    * @return
    * This RouterResponse object.
    */
  def setGenericError(message: Option[String]): RouterResponse = {
    status = ResponseException
    this.message = message

    this
  }


  /**
    * Send the response to the client.
    *
    * @param data
    * The data that will be sent to the user.
    */
  def sendResponse(data: JsonResponse): Unit = {
    status match {
      case OK =>
        routingContext.response()
          .setStatusCode(OK_CODE)
          .setChunked(true)
          .putHeader("Content-Type", "application/json")
          .write(data.asJson.noSpaces)
          .end()
      case ResponseException =>
        routingContext.response()
          .setStatusCode(EXCEPTION_CODE)
          .setChunked(true)
          .putHeader("Content-Type", "application/json")
          .write(Error(message).asJson.noSpaces)
          .end()
    }

  }

}
