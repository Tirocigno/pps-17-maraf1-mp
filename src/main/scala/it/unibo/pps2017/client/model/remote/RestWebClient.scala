
package it.unibo.pps2017.client.model.remote

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.web.client.{HttpResponse, WebClient}
import it.unibo.pps2017.client.controller.ClientController
import it.unibo.pps2017.commons.remote.API.RestAPI
import it.unibo.pps2017.commons.remote.RestUtils.{IPAddress, Port, ServerContext, formats}
import it.unibo.pps2017.commons.remote.exceptions.NotValidHttpMethodException
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetServerAPI
import it.unibo.pps2017.server.model.ServerContextEncoder
import org.json4s.jackson.Serialization.read

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
    * Start a Rest RestAPI call.
    *
    * @param apiToCall the RestAPI to call.
    */
  def callRemoteAPI(apiToCall: RestAPI): Unit

  def getCurrentServerContext: ServerContext = assignedServerContext.get
}

object RestWebClient {

  type AsyncResponse = Future[HttpResponse[Buffer]]

  private class RestWebClientImpl(override val discoveryServerContext: ServerContext) extends RestWebClient {

    override val webClient: WebClient = WebClient.create(Vertx.vertx())

    override def callRemoteAPI(apiToCall: RestAPI): Unit = checkOrSetServer(apiToCall)

    /**
      * Check if assigned server is setted, if not, makes a Rest call to the discovery server in order to get one,
      * then it start the rest call initially requested.
      *
      * @param restAPI the RestAPI to call.
      */
    def checkOrSetServer(restAPI: RestAPI): Unit = assignedServerContext match {
      case Some(context) => invokeAPI(context.port, context.ipAddress, restAPI)
      case None => callAPIAsAFuture(discoveryServerContext.port, discoveryServerContext.IPAddress,
        GetServerAPI)
        .map(getResponseBody)
        .map(deserializeServerContext)
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
    private def callAPIAsAFuture(port: Port, host: IPAddress, api: RestAPI) =
      api.httpMethod match {
      case HttpMethod.POST => webClient.post(port, host, api.path).sendFuture()
      case HttpMethod.GET => webClient.get(port, host, api.path).sendFuture()
      case _ => throw new NotValidHttpMethodException()
    }

    /**
      * Retrieve the body of an async response as a Future[String], if body is not present, throw NoSuchField exception.
      */
    private def getResponseBody(response: HttpResponse[Buffer]): String =
      response.bodyAsString().getOrElse(throw new NoSuchFieldException("Response body not found"))

    /**
      * Deserialize a json string and return a ServerContext.
      *
      * @param jsonSource the source to deserialize.
      * @return a ServerContext object.
      */
    private def deserializeServerContext(jsonSource: String): ServerContext = read[ServerContextEncoder](jsonSource)


    private def invokeAPI(port: Port, host: IPAddress, api: RestAPI): Unit = {

    }
  }

}
