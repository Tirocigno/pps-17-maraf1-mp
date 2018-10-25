package it.unibo.pps2017.server

import org.json4s.DefaultFormats

package object model {

  val DEFAULT_REDIS_HOST: String = "localhost"
  val DEFAULT_REDIS_PORT: Int = 6379
  val DEFAULT_REDIS_PW: Option[String] = None

  implicit val formats: DefaultFormats.type = DefaultFormats
}
