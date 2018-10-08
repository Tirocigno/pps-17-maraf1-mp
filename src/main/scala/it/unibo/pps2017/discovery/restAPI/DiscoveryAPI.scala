
package it.unibo.pps2017.discovery.restAPI

import io.vertx.core.http.HttpMethod

object DiscoveryAPI {

  sealed trait DiscoveryAPI {
    def path:String
    def httpMethod:HttpMethod
  }

  case object RegisterServerAPI extends DiscoveryAPI {

    override def path: String = "/registerserver"

    override def httpMethod: HttpMethod = HttpMethod.POST
  }

  case object getServerAPI extends DiscoveryAPI {

    override def path: String = "/getserver"

    override def httpMethod: HttpMethod = HttpMethod.GET
  }

}
