
package it.unibo.pps2017

import io.vertx.scala.core.net.SocketAddress
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.MatchesSetEncoder
import it.unibo.pps2017.utils.remote.RestUtils.{MatchRef, ServerContext}

import scala.language.implicitConversions

package object discovery {

  implicit class RichRoutingContext(route: RoutingContext) {
    def senderSocket: SocketAddress = route.request().remoteAddress()
  }

  implicit def generateServerContextFromRouter(source: SocketAddress): ServerContext = {
    ServerContext(source.host(), source.port())
  }

  implicit def matchesSetToJson(matchesSet:Set[MatchRef]):MatchesSetEncoder =
    MatchesSetEncoder(matchesSet)

}
