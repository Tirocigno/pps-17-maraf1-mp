
package it.unibo.pps2017.discovery

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.commons.remote.akka.AkkaClusterUtils
import it.unibo.pps2017.commons.remote.rest.RestUtils.Port
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI._
import it.unibo.pps2017.discovery.structures.{MatchesSet, ServerMap, SocialActorsMap}
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse}

/**
  * Basic trait for a server RestUtils implementation.
  */
trait ServerDiscovery extends ScalaVerticle{

  /**
    * This method deploy all the rest api exposed by the verticle.
    */
  def developAPI():Unit

  /**
    * Starts the seed for the cluster.
    */
  def startAkkaCluster(ipAddress: String): Unit = AkkaClusterUtils.startSeedCluster(ipAddress)

}

object ServerDiscovery {
  /**
    * Alias for a request handler.
    */
  type APIHandler = (RoutingContext, RouterResponse) => Unit

  def apply(port: Port, timeout: Int): ServerDiscovery = new ServerDiscoveryImpl(port, timeout)



private class ServerDiscoveryImpl(port: Port, timeout: Int) extends ServerDiscovery {

  val serverMap: ServerMap = ServerMap()
  val matchesSet: MatchesSet = MatchesSet()
  val socialActorsMap: SocialActorsMap = SocialActorsMap()

  /**
    * Handler for the GetServerAPI
    */
  private val getServerAPIHandler: APIHandler = (_, response) => {
    serverMap.getLessBusyServer match {
      case Some(server) => response.sendResponse(server)
      case _ => setErrorAndRespond(response, GetServerAPI.errorMessage)
    }
  }

  /**
    * Handler for the RegisterServerAPI.
    */
  private val registerServerAPIHandler: APIHandler = (router, response) => {
    serverMap.addServer(router)
    setMessageAndRespond(response, RegisterServerAPI.okMessage)
  }
  /**
    * Handler for the IncreaseServerMatchesAPI.
    */
  private val increaseServerMatchesAPIHandler: APIHandler = (router, response) => {
    try {
      serverMap.increaseMatchesPlayedOnServer(router)
      setMessageAndRespond(response, IncreaseServerMatchesAPI.okMessage)
    } catch {
      case e: IllegalArgumentException => setErrorAndRespond(response, IncreaseServerMatchesAPI.errorMessage)
    }
  }
  /**
    * Handler for the DecreaseServerMatchesAPI
    */
  private val decreaseServerMatchesAPIHandler: APIHandler = (router, response) => {
    try {
      serverMap.decreaseMatchesPlayedOnServer(router)
      setMessageAndRespond(response, DecreaseServerMatchesAPI.okMessage)
    } catch {
      case _: IllegalArgumentException => setErrorAndRespond(response,
        DecreaseServerMatchesAPI.noServerErrorMessage)
      case _: IllegalStateException => setErrorAndRespond(response,
        DecreaseServerMatchesAPI.noMatchErrorMessage)
    }
  }
  /**
    * Handler for the GetMatchesSetAPI.
    */
  private val getMatchesSetAPIHandler: APIHandler = (_, response) => {
    val returnSet = matchesSet.getAllMatches
    response.sendResponse(returnSet)
  }
  /**
    * Handler for the RegisterMatchAPi.
    */
  private val registerMatchAPIHandler: APIHandler = (router, response) => {
    try {
      val matchId = router.request().getFormAttribute(RegisterMatchAPI.MATCH_ID_KEY)
        .getOrElse(throw new IllegalStateException())
      matchesSet.addMatch(matchId)
      increaseServerMatchesAPIHandler(router, response)
    } catch {
      case _: IllegalStateException => setErrorAndRespond(response,
        RegisterMatchAPI.errorMessage)
    }
  }
  /**
    * Handler for the RemoveMatchAPI.
    */
  private val removeMatchAPIHandler: APIHandler = (router, response) => {
    try {
      val matchId = router.request().getFormAttribute(RegisterMatchAPI.MATCH_ID_KEY)
        .getOrElse(throw new NoSuchFieldException())
      matchesSet.removeMatch(matchId)
      decreaseServerMatchesAPIHandler(router, response)
    } catch {
      case _: NoSuchFieldException => setErrorAndRespond(response,
        RemoveMatchAPI.errorMessage)
    }
  }

  /**
    * Handler for RegisterSocialIDAPI
    */
  private val registerSocialIDAPI: APIHandler = (router, response) => {
    try {
      val playerID = router.request().getFormAttribute(RegisterSocialIDAPI.SOCIAL_ID)
        .getOrElse(throw new NoSuchFieldException())
      val actorRef = router.request().getFormAttribute(RegisterSocialIDAPI.SOCIAL_ACTOR)
        .getOrElse(throw new NoSuchFieldException())
      socialActorsMap.registerUser(playerID, actorRef)
      setMessageAndRespond(response, RegisterSocialIDAPI.okMessage)
    } catch {
      case _: NoSuchFieldException => setErrorAndRespond(response, RegisterSocialIDAPI.errorMessage)
    }
  }

  /**
    * Handler for UnregisterSocialIDAPI
    */
  private val unregisterSocialIDAPI: APIHandler = (router, response) => {
    try {
      val playerID = router.request().getFormAttribute(UnregisterSocialIDAPI.SOCIAL_ID)
        .getOrElse(throw new NoSuchFieldException())
      socialActorsMap.unregisterUser(playerID)
      setMessageAndRespond(response, UnregisterSocialIDAPI.okMessage)
    } catch {
      case _: NoSuchFieldException => setErrorAndRespond(response, UnregisterSocialIDAPI.errorMessage)
    }
  }

  /**
    * Handler for GetAllOnlinePlayersAPI.
    */
  private val getAllOnlinePlayersAPI: APIHandler = (_, response) => {
    response.sendResponse(socialActorsMap.getCurrentOnlinePlayerMap)
  }

  /**
    * Private method to send an OK response with a message.
    *
    * @param response the response to complete.
    * @param message  the message to insert in the body.
    */
  private def setMessageAndRespond(response: RouterResponse, message: String): Unit = {
    response.sendResponse(Message(message))
  }



  /**
    * Private method to set an error inside the body of a response call and send it.
    *
    * @param response the response to complete with an error.
    * @param body     the description of the error.
    */
  private def setErrorAndRespond(response: RouterResponse, body: String): Unit =
    response.setGenericError(Some(body))
      .sendResponse(Error())

  /**
    * Mock handler.
    */
  private def mockHandler: (RoutingContext, RouterResponse) => Unit = (_, res) =>
    res.sendResponse(Message("RestAPI CALLED"))

  override def start(): Unit = developAPI()

  override def developAPI(): Unit = {
    val router = Router.router(vertx)
    DiscoveryAPI.values.map({
      case api@GetServerAPI => api.asRequest(router, getServerAPIHandler)
      case api@RegisterServerAPI => api.asRequest(router,
        registerServerAPIHandler)
      case api@IncreaseServerMatchesAPI => api.asRequest(router,
        increaseServerMatchesAPIHandler)
      case api@DecreaseServerMatchesAPI => api.asRequest(router,
        decreaseServerMatchesAPIHandler)
      case api@GetAllMatchesAPI => api.asRequest(router,
        getMatchesSetAPIHandler)
      case api@RegisterMatchAPI => api.asRequest(router,
        registerMatchAPIHandler)
      case api@RemoveMatchAPI => api.asRequest(router,
        removeMatchAPIHandler)
      case api@RegisterSocialIDAPI => api.asRequest(router,
        registerSocialIDAPI)
      case api@UnregisterSocialIDAPI => api.asRequest(router,
        unregisterSocialIDAPI)
      case api@GetAllOnlinePlayersAPI => api.asRequest(router,
        getAllOnlinePlayersAPI)
      case api@_ => api.asRequest(router, mockHandler)
    })

    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(timeout)

    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(port)
  }

}
}
