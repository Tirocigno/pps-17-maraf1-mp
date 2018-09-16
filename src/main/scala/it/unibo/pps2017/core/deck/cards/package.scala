
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.Seed.{Club, Seed, Sword}

/**
  * Package object for Card package.
  */
package object cards {

  val defaultSeed: Seed = Sword
  val otherSeed: Seed = Club
  val minorValue: Int = 5
  val majorValue: Int = 2
}
