
package it.unibo.pps2017.core.deck.cards

import it.unibo.pps2017.core.deck.cards.Seed.Seed

/**
  * This trait define the basic behaviour of a card.
  * A card has a seed and a value.
  */

trait Card {
  def seed: Seed

  def value: Int
}
