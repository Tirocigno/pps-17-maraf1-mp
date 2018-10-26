
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.Seed.{Club, Seed, Sword}

/**
  * Package object for Card package.
  */
package object cards {

  val DEFAULT_SEED: Seed = Sword
  val OTHER_SEED: Seed = Club
  val MINOR_VALUE: Int = 5
  val MAJOR_VALUE: Int = 2
  val SWITCH_VALUE = 4
}
