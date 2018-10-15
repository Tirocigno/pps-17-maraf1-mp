
package it.unibo.pps2017.commons

import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.model.{GET, POST, Request, RouterResponse}

object API {

  /**
    * Trait of DiscoveryAPI.
    */
  trait API {
    /**
      * Path of the API
      *
      * @return a string containing the path of API.
      */
    def path: String

    /**
      * Http method of API.
      *
      * @return an HTTP method to call.
      */
    def httpMethod: HttpMethod

    /**
      * Convert the API to a request object to register into a router.
      *
      * @param router the router on which request will be registered.
      * @param handle the handler of the request.
      * @return a Request object build from the API.
      */
    def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request
  }
}