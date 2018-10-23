
package it.unibo.pps2017

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.commons.remote.RestUtils.{MatchRef, ServerContext}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.StandardParameters
import it.unibo.pps2017.server.model.MatchesSetEncoder

import scala.language.implicitConversions

package object discovery {

  /**
    * Transform a vertx router to a ServerContext, retrieving the ip address from the socket and the port from
    * parameter inside the request.
    *
    * @param router the RoutingContext of the request to handle
    * @return a ServerContext to serialize.
    */
  implicit def retrieveContextFromRequest(router: RoutingContext): ServerContext = {
    val request = router.request()
    val socketIP = request.remoteAddress().host()
    val socketPort = Integer.valueOf(request.getFormAttribute(StandardParameters.PORT_KEY).get)
    ServerContext(socketIP, socketPort)
  }

  implicit def matchesSetToJson(matchesSet:Set[MatchRef]):MatchesSetEncoder =
    MatchesSetEncoder(matchesSet)

}
