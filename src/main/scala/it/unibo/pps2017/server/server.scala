
package it.unibo.pps2017.server

import org.json4s.DefaultFormats

package object server {

  val systemName = "Maraf1mp"
  val AKKA_CONFIG_FILE = "serverApplication"
  val AKKA_CLUSTER_PORT: Option[Int] = None
  implicit val formats: DefaultFormats.type = DefaultFormats

}
