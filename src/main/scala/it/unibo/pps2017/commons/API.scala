
package it.unibo.pps2017.commons

import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.server.model.{GET, POST, Request, RouterResponse}

object API {

  /**
    * Trait of DiscoveryAPI.
    */
  sealed trait API {
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

  /**
    * API for searching
    */
  case object FoundGameAPI extends API {

    override def path: String = "/foundGame"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)
  }


  /**
    * API for test a good communication.
    */
  case object HelloAPI extends API {

    override def path: String = "/"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  /**
    * API for test a wrong communication.
    */
  case object ErrorAPI extends API {

    override def path: String = "/error"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  /**
    * API for retrieve a single game.
    */
  case object GameAPI extends API {

    override def path: String = "/game/:gameId"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  /**
    * values method, analog to java's enumeration's values() method.
    *
    * @return a Set containing all the objects in the DiscoveryAPI object.
    */
  def values: Set[API] = Set(HelloAPI, ErrorAPI, GameAPI, FoundGameAPI)
}