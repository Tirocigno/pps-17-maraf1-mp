
package it.unibo.pps2017.client.model.remote

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.client.HttpResponse
import it.unibo.pps2017.client.controller.ClientController
import it.unibo.pps2017.commons.remote.API.RestAPI
import it.unibo.pps2017.commons.remote.RestUtils.{ServerContext, formats}
import it.unibo.pps2017.commons.remote.exceptions.NotValidHttpMethodException
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetServerAPI
import it.unibo.pps2017.server.model.ServerApi.FoundGameRestAPI$
import it.unibo.pps2017.server.model.{GameFound, GetRequest, PostRequest, ServerContextEncoder}
import org.json4s.jackson.Serialization.read

import scala.concurrent.Future
import scala.language.postfixOps

/**
  * This module is responsable for sending remote calls via rest apis and
  * handle their responses.
  */
sealed trait RestWebClient {

  val discoveryServerContext: ServerContext
  val clientController: ClientController = ClientController getSingletonController
  var assignedServerContext: Option[ServerContext] = None

  /**
    * Start a Rest RestAPI call.
    *
    * @param apiToCall the RestAPI to call.
    */
  def callRemoteAPI(apiToCall: RestAPI, paramMap: Option[Map[String, Any]]): Unit

  def getCurrentServerContext: ServerContext = assignedServerContext.get
}

object RestWebClient {

  type AsyncResponse = Future[HttpResponse[Buffer]]

  def apply(discoveryServerContext: ServerContext): RestWebClient = new RestWebClientImpl(discoveryServerContext)

  private class RestWebClientImpl(override val discoveryServerContext: ServerContext) extends RestWebClient {


    override def callRemoteAPI(apiToCall: RestAPI, paramMap: Option[Map[String, Any]]): Unit =
      checkOrSetServer(apiToCall, paramMap)

    /**
      * Check if assigned server is setted, if not, makes a Rest call to the discovery server in order to get one,
      * then it start the rest call initially requested.
      *
      * @param restAPI the RestAPI to call.
      */
    def checkOrSetServer(restAPI: RestAPI, paramMap: Option[Map[String, Any]]): Unit = assignedServerContext match {
      case Some(_) => executeAPICall(restAPI, paramMap)
      case None => GetRequest(discoveryServerContext.ipAddress, GetServerAPI.path,
        getServerAPIHandler(restAPI)(paramMap), reportErrorToController, None, Some(discoveryServerContext.port))
    }

    /**
      * Handle a response for a GetRequest API, can be used as a Option[String] => Unit function if the parameters
      * are passed correctly.
      *
      * @param apiToInvokeWhenReady the api to invoke when the server to contact is configured.
      * @param paramMap             the map of parameters needed in the request to invoke.
      * @param jsonSource           the result of the GetServer call as a string.
      */
    private def getServerAPIHandler(apiToInvokeWhenReady: RestAPI)(paramMap: Option[Map[String, Any]])
                                   (jsonSource: Option[String]): Unit = {
      assignedServerContext = Some(deserializeServerContext(jsonSource.get))
      executeAPICall(apiToInvokeWhenReady, paramMap)
    }


    /**
      * Catch an error and it report back to controller.
      *
      * @param throwable the throwable to notify.
      */
    private def reportErrorToController(throwable: Throwable): Unit = clientController.notifyError(throwable)


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

    /**
      * Execute an api call switching between all the possible API.
      *
      * @param api             the api to execute.
      * @param paramMap        the parameters to pass to the request.
      */
    private def executeAPICall(api: RestAPI, paramMap: Option[Map[String, Any]]): Unit = api match {
      case FoundGameRestAPI$ => invokeAPI(api, paramMap, handleFoundGameRestAPI)
      case _ => println("Api call executed")

    }

    /**
      * Create a request and send it to the specified server.
      *
      * @param api             the api to call
      * @param paramMap        the parameters inside the request.
      * @param successCallBack the callback to resume when the response is ready.
      */
    private def invokeAPI(api: RestAPI, paramMap: Option[Map[String, Any]],
                          successCallBack: Option[String] => Unit): Unit = {
      val context = assignedServerContext.get
      api.httpMethod match {
        case HttpMethod.POST => PostRequest(context.ipAddress, api.path, successCallBack, reportErrorToController,
          paramMap, Some(context.port))
        case HttpMethod.GET => GetRequest(context.ipAddress, api.path, successCallBack, reportErrorToController,
          paramMap, Some(context.port))
        case _ => throw new NotValidHttpMethodException()
      }

    }

    /**
      * Handler for the FoundGame API response.
      *
      * @param jSonSource the body of the response.
      */
    private def handleFoundGameRestAPI(jSonSource: Option[String]): Unit = {
      val gameID = read[GameFound](jSonSource.get).gameId
      clientController.setGameID(gameID)
    }
  }

}
