
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

  /**
    * API to register a new server on discovery server.
    */
  case object RegisterServerAPI extends DiscoveryAPI {

    override def path: String = "/registerserver"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router,path,handle)
  }

  /**
    * API to retrieve less busy server.
    */
  case object GetServerAPI extends DiscoveryAPI {

    override def path: String = "/getserver"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router,path,handle)
  }

  /**
    * API to increase the number of matches on a specified server.
    */
  case object IncreaseServerMatches extends DiscoveryAPI {

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)

    override def path: String = "/increaseservermatches"
  }

  /**
    * values method, analog to java's enumeration's values() method.
    *
    * @return a Set containing all the objects in the DiscoveryAPI object.
    */
  def values: Set[DiscoveryAPI] = Set(GetServerAPI, RegisterServerAPI, IncreaseServerMatches, DecreaseServerMatches)

  /**
    * API to decrease the number of matches on a specified server.
    */
  case object DecreaseServerMatches extends DiscoveryAPI {

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)

    override def path: String = "/decreaseservermatches"
  }

  case object RegisterMatch extends DiscoveryAPI {

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)

    override def path: String = "/registermatch"
  }

  case object RemoveMatch extends DiscoveryAPI {

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)

    override def path: String = "/removermatch"
  }

  case object GetMatch extends DiscoveryAPI {

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)

    override def path: String = "/getmatch"
  }

  case object GetAllMatches extends DiscoveryAPI {

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)

    override def path: String = "/getallmatches"
  }


}
