
package it.unibo.pps2017

package object discovery {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  type ServerContext = (IPAddress,Port)

  implicit class RichServerContext(serverContext: ServerContext) {
    def ipAddress:IPAddress = serverContext._1
    def port:Port = serverContext._2
  }
}
