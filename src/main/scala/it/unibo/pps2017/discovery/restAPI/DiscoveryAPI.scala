
package it.unibo.pps2017.discovery.restAPI

import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.model.{GET, POST, Request, RouterResponse}

object DiscoveryAPI {

  sealed trait DiscoveryAPI {
    def path:String
    def httpMethod:HttpMethod
    def asRequest(router: Router, handle:(RoutingContext, RouterResponse) => Unit):Request
  }

  case object RegisterServerAPI extends DiscoveryAPI {

    override def path: String = "/registerserver"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router,path,handle)
  }

  case object getServerAPI extends DiscoveryAPI {

    override def path: String = "/getserver"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router,path,handle)
  }

  def values:Set[DiscoveryAPI] = Set(getServerAPI, RegisterServerAPI)


}
