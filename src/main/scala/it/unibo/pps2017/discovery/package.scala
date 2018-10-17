
package it.unibo.pps2017

import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.commons.remote.RestUtils.{MatchRef, ServerContext}
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.GetServerAPI
import it.unibo.pps2017.server.model.MatchesSetEncoder

import scala.language.implicitConversions

package object discovery {

  implicit def retreiveContextFromRequest(router: RoutingContext): ServerContext = {
    val request = router.request()
    ServerContext(request.getFormAttribute(GetServerAPI.getIpKey).get,
      Integer.valueOf(request.getFormAttribute(GetServerAPI.getPortKey).get))
  }

  implicit def matchesSetToJson(matchesSet:Set[MatchRef]):MatchesSetEncoder =
    MatchesSetEncoder(matchesSet)

}
