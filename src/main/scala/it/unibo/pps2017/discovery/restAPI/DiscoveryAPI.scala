
package it.unibo.pps2017.discovery.restAPI

import io.vertx.core.http.HttpMethod

object DiscoveryAPI {

  sealed trait DiscoveryAPI {
    def path:String
    def httpMethod:HttpMethod
  }

}
