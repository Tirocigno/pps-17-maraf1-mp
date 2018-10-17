
package it.unibo.pps2017.discovery

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.commons.remote.RestUtils.Port
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI._
import it.unibo.pps2017.discovery.structures.{MatchesSet, ServerMap}
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse}

/**
  * Basic trait for a server RestUtils implementation.
  */
trait ServerDiscovery extends ScalaVerticle{

  def developAPI():Unit

}

object ServerDiscovery {
  type APIHandler = (RoutingContext, RouterResponse) => Unit

  def apply(port: Port, timeout: Int): ServerDiscovery = new ServerDiscoveryImpl(port, timeout)


private class ServerDiscoveryImpl(port: Port, timeout: Int) extends ServerDiscovery {

  val serverMap: ServerMap = ServerMap()
  val matchesSet: MatchesSet = MatchesSet()


  private def setErrorAndRespond(response: RouterResponse, body: String): Unit =
    response.setError().sendResponse(Error(Some(body)))

  private val getServerAPIHandler: APIHandler = (_, response) => {
    serverMap.getLessBusyServer match {
      case Some(server) => response.sendResponse(server)
      case _ => setErrorAndRespond(response, "NO SERVER FOUND")
    }
  }

  private val registerServerAPIHandler: APIHandler = (router, response) => {
    serverMap.addServer(router.senderSocket)
    response.sendResponse(Message("SERVER REGISTERED SUCCESSFULLY"))
  }

  private val increaseServerMatchesAPIHandler: APIHandler = (router, response) => {
    try {
      serverMap.increaseMatchesPlayedOnServer(router.senderSocket)
      response.sendResponse(Message("INCREASE MATCHES ON SERVER"))
    } catch {
      case e: IllegalArgumentException => setErrorAndRespond(response, "BAD REQUEST")
    }
  }

  private val decreaseServerMatchesAPIHandler: APIHandler = (router, response) => {
    try {
      serverMap.decreaseMatchesPlayedOnServer(router.senderSocket)
      response.sendResponse(Message("DECREASED MATCHES ON SERVER"))
    } catch {
      case e: IllegalArgumentException => setErrorAndRespond(response, e.getMessage)
      case e: IllegalStateException => setErrorAndRespond(response, e.getMessage)
    }
  }

  private val getMatchesSetAPIHandler: APIHandler = (_, response) => {
    val returnSet = matchesSet.getAllMatches
    response.sendResponse(returnSet)
  }

  private val registerMatchAPIHandler: APIHandler = (router, response) => {
    try {
      val matchId = router.request().getFormAttribute(RegisterMatchAPI.matchIdKey)
        .getOrElse(throw new IllegalStateException())
      matchesSet.addMatch(matchId)
      response.sendResponse(Message("MATCH ADDED SUCCESSFULLY"))
    } catch {
      case _: IllegalStateException => setErrorAndRespond(response,
        "NO MATCHID FOUND IN REQUEST")
    }
  }

  private val removeMatchAPIHandler: APIHandler = (router, response) => {
    try {
      val matchId = router.request().getFormAttribute(RegisterMatchAPI.matchIdKey)
        .getOrElse(throw new IllegalStateException())
      matchesSet.removeMatch(matchId)
      decreaseServerMatchesAPIHandler(router, response)
    } catch {
      case _: IllegalStateException => setErrorAndRespond(response,
        "NO MATCHID FOUND IN REQUEST")
    }
  }

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
