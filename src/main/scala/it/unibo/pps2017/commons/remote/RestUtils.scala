
package it.unibo.pps2017.commons.remote

import it.unibo.pps2017.server.model.ServerContextEncoder
import org.json4s.DefaultFormats

import scala.language.implicitConversions

object RestUtils {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  case class ServerContext(IPAddress: IPAddress, port: Port)

  /**
    * Implicit val containing the format for Json deserialization.
    */
  implicit val formats: DefaultFormats.type = DefaultFormats

  /**
    * Implicit conversion from server context to serverContextEncoder, a object which can be serialized.
    *
    * @param serverContext the server context to serialize.
    * @return a ServerContextEncoder which can be encoded in Json format.
    */
  implicit def serverContextEncoderConversion(serverContext: ServerContext): ServerContextEncoder =
    ServerContextEncoder(serverContext.IPAddress, serverContext.port)

  /**
    * Implicit conversion from serverContextEncoder to ServerContext.
    *
    * @param serverContext the server context to deserialize.
    * @return a ServerContext object derived from source.
    */
  implicit def serverContextDecoderConversion(serverContext: ServerContextEncoder): ServerContext =
    ServerContext(serverContext.ipAddress, serverContext.port)

}
