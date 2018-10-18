package it.unibo.pps2017.client

import scala.util.Random

package object controller {

  val PLAYER_ID = "player:"

  /**
    * Create a random player id
    *
    * @return a string containing a random player id.
    */
  def getRandomID: String = PLAYER_ID + Random.nextInt()
}
