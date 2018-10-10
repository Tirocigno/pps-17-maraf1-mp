
package it.unibo.pps2017.discovery

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.discovery.ServerDiscovery.APIHandler
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{DecreaseServerMatches, GetServerAPI, IncreaseServerMatches, RegisterServerAPI}
import it.unibo.pps2017.discovery.structures.{MatchesSet, ServerMap}
import it.unibo.pps2017.server.controller.Dispatcher.{PORT, TIMEOUT}
import it.unibo.pps2017.server.model.{Error, Message, RouterResponse}

/**
  * Basic trait for a server discovery implementation.
  */
trait ServerDiscovery extends ScalaVerticle{

  def developAPI():Unit

  def handleRestCall():Unit

  def addMockServer(IPAddress: IPAddress, port: Port): Unit

}

object ServerDiscovery {
  type APIHandler = (RoutingContext, RouterResponse) => Unit
  def apply(): ServerDiscovery = new ServerDiscoveryImpl()
}

private class ServerDiscoveryImpl extends ServerDiscovery {

  val serverMap: ServerMap = ServerMap()
  val matchesSet:MatchesSet = MatchesSet()

  private val getServerAPIHandler: APIHandler = (_, response) => {
    serverMap.getLessBusyServer match {
      case Some(server) => response.sendResponse(server)
      case _ => response.sendResponse(Error(Some("NO SERVER FOUND")))
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
      case e: IllegalArgumentException => response.sendResponse(Error(Some(e.getMessage)))
    }
  }

  private val decreaseServerMatchesAPIHandler: APIHandler = (router, response) => {
    try {
      serverMap.increaseMatchesPlayedOnServer(router.senderSocket)
      response.sendResponse(Message("DECREASED MATCHES ON SERVER"))
    } catch {
      case e: IllegalArgumentException => response.sendResponse(Error(Some(e.getMessage)))
      case e: IllegalStateException => response.sendResponse(Error(Some(e.getMessage)))
    }
  }

  private def mockHandler:(RoutingContext, RouterResponse) => Unit = (_,_) => println("Api called")

  override def start(): Unit = developAPI()

  override def developAPI(): Unit = {
    val router = Router.router(vertx)
    DiscoveryAPI.values.map({
      case api@GetServerAPI => api.asRequest(router, getServerAPIHandler)
      case api@RegisterServerAPI => api.asRequest(router, registerServerAPIHandler)
      case api@IncreaseServerMatches => api.asRequest(router, increaseServerMatchesAPIHandler)
      case api@DecreaseServerMatches => api.asRequest(router, decreaseServerMatchesAPIHandler)
      case api @ _ => api.asRequest(router,mockHandler)
    })

    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(TIMEOUT)

    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(PORT)
  }

  override def handleRestCall(): Unit = ???

  override def addMockServer(IPAddress: IPAddress, port: Port): Unit =
    serverMap.addServer(ServerContext(IPAddress, port))
}
