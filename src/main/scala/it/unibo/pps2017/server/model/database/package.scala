package it.unibo.pps2017.server.model

package object database {
  def getUserKey(username: String): String = "user:" + username
}
