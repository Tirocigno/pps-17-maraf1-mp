
package it.unibo.pps2017

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.commons.remote.rest.RestUtils.{MatchRef, ServerContext}
import it.unibo.pps2017.commons.remote.social.SocialUtils.SocialMap
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.StandardParameters
import it.unibo.pps2017.server.model.{MatchesSetEncoder, OnlinePlayersMapEncoder}

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

  /**
    * Implicit conversion for set of matches to a serializable object.
    *
    * @param matchesSet the set to return in response.
    * @return a serializable version of the set.
    */
  implicit def matchesSetToJson(matchesSet:Set[MatchRef]):MatchesSetEncoder =
    MatchesSetEncoder(matchesSet)

  implicit def socialActorMapToJson(socialMap: SocialMap): OnlinePlayersMapEncoder =
    OnlinePlayersMapEncoder(socialMap)

}
