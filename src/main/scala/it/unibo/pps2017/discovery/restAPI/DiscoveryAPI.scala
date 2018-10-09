
package it.unibo.pps2017.discovery.restAPI

import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.model.{GET, POST, Request, RouterResponse}

object DiscoveryAPI {

  /**
    * Trait of DiscoveryAPI.
    */
  sealed trait DiscoveryAPI {
    /**
      * Path of the API
      * @return a string containing the path of API.
      */
    def path:String

    /**
      * Http method of API.
      * @return an HTTP method to call.
      */
    def httpMethod:HttpMethod

    /**
      * Convert the API to a request object to register into a router.
      * @param router the router on which request will be registered.
      * @param handle the handler of the request.
      * @return a Request object build from the API.
      */
    def asRequest(router: Router, handle:(RoutingContext, RouterResponse) => Unit):Request
  }

  case object RegisterServerAPI extends DiscoveryAPI {

    override def path: String = "/registerserver"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router,path,handle)
  }

  case object GetServerAPI extends DiscoveryAPI {

    override def path: String = "/getserver"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router,path,handle)
  }

  def values: Set[DiscoveryAPI] = Set(GetServerAPI, RegisterServerAPI)

}
