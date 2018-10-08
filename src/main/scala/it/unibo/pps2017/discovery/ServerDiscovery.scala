
package it.unibo.pps2017.discovery

import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.http.HttpServerOptions
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.RegisterServerAPI
import it.unibo.pps2017.server.controller.Dispatcher.{PORT, TIMEOUT}
import it.unibo.pps2017.server.model.{Message, RouterResponse}

/**
  * Basic trait for a server discovery implementation.
  */
trait ServerDiscovery extends ScalaVerticle{

  def developAPI():Unit

  def handleRestCall():Unit

}

object ServerDiscovery {
  def apply(): ServerDiscovery = new ServerDiscoveryImpl()
}

private class ServerDiscoveryImpl extends ServerDiscovery {

  val serverMap:ServerMap = ???
  val matchesSet:MatchesSet = MatchesSet()

  override def start(): Unit = developAPI()

  private def mockHandler:(RoutingContext, RouterResponse) => Unit = (_,_) => println("Api called")

  override def developAPI(): Unit = {
    val router = Router.router(vertx)
    DiscoveryAPI.values.map({
      case api @ RegisterServerAPI => api.asRequest(router,getServerAPIHandler)
      case api @ _ => api.asRequest(router,mockHandler)
    })
    val options = HttpServerOptions()
    options.setCompressionSupported(true)
      .setIdleTimeout(TIMEOUT)


    vertx.createHttpServer(options)
      .requestHandler(router.accept _).listen(PORT)
  }

  private val getServerAPIHandler:(RoutingContext, RouterResponse) => Unit = (request,response) => {
    response.sendResponse(Message(serverMap.getLessBusyServer))
  }

  override def handleRestCall(): Unit = ???
}
