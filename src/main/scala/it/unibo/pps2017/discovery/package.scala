
package it.unibo.pps2017

import it.unibo.pps2017.server.model.JsonResponse

package object discovery {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  type ServerContext = (IPAddress,Port)

  implicit class RichServerContext(serverContext: ServerContext) extends JsonResponse {
    def ipAddress:IPAddress = serverContext._1
    def port:Port = serverContext._2
  }
}
