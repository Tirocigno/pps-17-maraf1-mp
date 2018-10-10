
package it.unibo.pps2017

import io.vertx.scala.core.net.SocketAddress
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.pps2017.server.model.ServerContextEncoder

package object discovery {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  case class ServerContext(IPAddress: IPAddress, port: Port) {
    override def hashCode(): Port = super.hashCode()

    override def equals(obj: Any): Boolean = obj match {
      case ServerContext(otherAddress, otherPort) => IPAddress.equals(otherAddress) && port == otherPort
      case _ => false
    }
  }

  implicit class RichRoutingContext(route: RoutingContext) {
    def senderSocket: SocketAddress = route.request().remoteAddress()
  }

  implicit def generateServerContextFromRouter(source: SocketAddress): ServerContext = {
    ServerContext(source.host(), source.port())
  }

  implicit def serverContextToJson(serverContext: ServerContext): ServerContextEncoder =
    ServerContextEncoder(serverContext.IPAddress, serverContext.port)
}
