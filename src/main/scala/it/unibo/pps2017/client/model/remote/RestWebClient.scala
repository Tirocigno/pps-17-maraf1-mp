
package it.unibo.pps2017.client.model.remote

import it.unibo.pps2017.client.controller.{ClientController, SingletonClientController}
import it.unibo.pps2017.utils.remote.RestAPI
import it.unibo.pps2017.utils.remote.RestUtils.ServerContext

/**
  * This module is responsable for sending remote calls via rest apis and
  * handle their responses.
  */
sealed trait RestWebClient {

  val discoveryServerContext: ServerContext
  val clientController: ClientController = SingletonClientController
  var assignedServerContext: Option[ServerContext] = None

  /**
    * Start a Rest API call.
    *
    * @param apiToCall the API to call.
    */
  def callRemoteAPI(apiToCall: RestAPI): Unit
}

object RestWebClient {

  private class RestWebClientImpl(override val
                                  discoveryServerContext: ServerContext)
    extends RestWebClient {
    override def callRemoteAPI(apiToCall: RestAPI): Unit = ???
  }

}
