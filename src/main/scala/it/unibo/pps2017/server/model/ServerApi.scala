
package it.unibo.pps2017.server.model

import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.pps2017.commons.remote.API.RestAPI

object ServerApi {
  /**
    * values method, analog to java's enumeration's values() method.
    *
    * @return a Set containing all the objects in the DiscoveryAPI object.
    */
  def values: Set[RestAPI] = Set(HelloRestAPI, ErrorRestAPI, GameRestAPI, FoundGameRestAPI, AddUserAPI, GetUserAPI)

  /**
    * RestAPI for searching
    */
  case object FoundGameRestAPI extends RestAPI {

    val meParamKey = "me"

    val partnerParam = "partner"

    override def path: String = "/foundGame"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)
  }

  /**
    * RestAPI for test a good communication.
    */
  case object HelloRestAPI extends RestAPI {

    override def path: String = "/"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  /**
    * RestAPI for test a wrong communication.
    */
  case object ErrorRestAPI extends RestAPI {

    override def path: String = "/error"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  /**
    * RestAPI for retrieve a single game.
    */
  case object GameRestAPI extends RestAPI {

    override def path: String = "/game/:gameId"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  case object AddUserAPI extends RestAPI {
    override def path: String = "/user/addUser/:username"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)
  }

  case object GetUserAPI extends RestAPI {
    override def path: String = "/user/:username"

    override def httpMethod: HttpMethod = HttpMethod.GET

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      GET(router, path, handle)
  }

  case object  AddFriendAPI extends RestAPI {

    val friendUsername: String = "friend"

    override def path: String = "/user/addFriend/:username"

    override def httpMethod: HttpMethod = HttpMethod.POST

    override def asRequest(router: Router, handle: (RoutingContext, RouterResponse) => Unit): Request =
      POST(router, path, handle)
  }
}
