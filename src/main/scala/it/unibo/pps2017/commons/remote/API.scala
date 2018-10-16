package it.unibo.pps2017.commons.remote

import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.model.{Request, RouterResponse}

object API {

  /**
    * Trait of DiscoveryAPI.
    */
  trait RestAPI {
    /**
      * Path of the RestAPI
      *
      * @return a string containing the path of RestAPI.
      */
    def path: String

    /**
      * Http method of RestAPI.
      *
      * @return an HTTP method to call.
      */
    def httpMethod: HttpMethod

    /**
      * Convert the RestAPI to a request object to register into a router.
      *
      * @param router the router on which request will be registered.
      * @param handle the handler of the request.
      * @return a Request object build from the RestAPI.
      */
    def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request
  }

}
