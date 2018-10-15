
package it.unibo.pps2017.client.model.remote

import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.WebClient
import it.unibo.pps2017.client.controller.ClientController
import it.unibo.pps2017.utils.remote.RestAPI
import it.unibo.pps2017.utils.remote.RestUtils.ServerContext

/**
  * This module is responsable for sending remote calls via rest apis and
  * handle their responses.
  */
sealed trait RestWebClient {

  val discoveryServerContext: ServerContext
  val clientController: ClientController = ClientController getSingletonController
  var assignedServerContext: Option[ServerContext] = None
  val webClient: WebClient

  /**
    * Start a Rest API call.
    *
    * @param apiToCall the API to call.
    */
  def callRemoteAPI(apiToCall: RestAPI): Unit
}

object RestWebClient {

  private class RestWebClientImpl(override val discoveryServerContext: ServerContext) extends RestWebClient {
    override val webClient: WebClient = WebClient.create(Vertx.vertx())
    override def callRemoteAPI(apiToCall: RestAPI): Unit = ???
  }

}
