
package it.unibo.pps2017

import it.unibo.pps2017.server.model.ServerContextEncoder

package object discovery {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  case class ServerContext(IPAddress: IPAddress, port: Port)

  implicit def serverContextToJson(serverContext: ServerContext): ServerContextEncoder =
    ServerContextEncoder(serverContext.IPAddress, serverContext.port)
}
