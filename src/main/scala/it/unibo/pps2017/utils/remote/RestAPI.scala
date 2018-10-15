
package it.unibo.pps2017.utils.remote

import io.vertx.core.http.HttpMethod

/**
  * Generic trait to be extended by every API.
  */
trait RestAPI {
  def HttpMethod: HttpMethod
}
