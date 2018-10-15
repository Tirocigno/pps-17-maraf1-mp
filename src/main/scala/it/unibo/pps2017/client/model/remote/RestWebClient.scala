
package it.unibo.pps2017.client.model.remote

import io.vertx.core.http.HttpMethod
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.WebClient
import it.unibo.pps2017.client.controller.ClientController
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetServerAPI
import it.unibo.pps2017.utils.remote.RestAPI
import it.unibo.pps2017.utils.remote.RestUtils.{IPAddress, Port, ServerContext}
import it.unibo.pps2017.utils.remote.exceptions.NotValidHttpMethodException

import scala.concurrent.ExecutionContext.Implicits.global

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

    override def callRemoteAPI(apiToCall: RestAPI): Unit = checkOrSetServer(apiToCall)

    def checkOrSetServer(restAPI: RestAPI): Unit = assignedServerContext match {
      case Some(context) => invokeAPI(context.port, context.ipAddress, restAPI)
      case None => callAPIAsAFuture(discoveryServerContext.port, discoveryServerContext.IPAddress,
        GetServerAPI)
        .map(_.bodyAsString().get)
        .map(ServerContext(_, 0))
        .onComplete {
          case scala.util.Success(server) => invokeAPI(server.port, server.IPAddress, restAPI)
          case scala.util.Failure(exception) => clientController.notifyError(exception)
        }
    }

    /**
      * This method execute an asynchronous call on the specified ip and port and return the result as a future.
      *
      * @param port the port opened by the host to contact.
      * @param host the ip address of the host to contact.
      * @param api  the api to call on that server.
      * @return a future containing an HttpResponse[Buffer] object to handle.
      */
    private def callAPIAsAFuture(port: Port, host: IPAddress, api: RestAPI) = api.httpMethod match {
      case HttpMethod.POST => webClient.post(port, host, api.path).sendFuture()
      case HttpMethod.GET => webClient.get(port, host, api.path).sendFuture()
      case _ => throw new NotValidHttpMethodException()
    }

    private def invokeAPI(port: Port, host: IPAddress, api: RestAPI): Unit = {

    }
  }

}
