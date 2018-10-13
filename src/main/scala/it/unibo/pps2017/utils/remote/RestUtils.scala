package it.unibo.pps2017.utils.remote

import it.unibo.pps2017.server.model.ServerContextEncoder

import scala.language.implicitConversions

object RestUtils {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  case class ServerContext(IPAddress: IPAddress, port: Port)

  implicit def serverContextToJson(serverContext: ServerContext): ServerContextEncoder =
    ServerContextEncoder(serverContext.IPAddress, serverContext.port)

}
