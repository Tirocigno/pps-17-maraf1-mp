
package it.unibo.pps2017.utils.remote

import io.vertx.core.http.HttpMethod

/**
  * Generic trait to be extended by every API.
  */
trait RestAPI {

  /**
    * Path of the API
    *
    * @return a string containing the path of API.
    */
  def path: String

  /**
    * Http method of API.
    *
    * @return an HTTP method to call.
    */
  def httpMethod: HttpMethod
}
