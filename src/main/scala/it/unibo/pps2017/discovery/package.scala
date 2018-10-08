
package it.unibo.pps2017

package object discovery {

  type IPAddress = String

  type Port = Int

  type MatchRef = String

  type ServerContext = (IPAddress,Port)

  implicit class RichServerContext(serverContext: ServerContext) {
    def ipAddress:IPAddress = serverContext._1
    def port:Port = serverContext._2

    override def hashCode(): Port = super.hashCode()

    override def equals(obj: Any): Boolean = obj match {
      case other:RichServerContext => other.ipAddress.equals(serverContext.ipAddress) && other.port == serverContext.port
      case _ => false
    }
  }
}
