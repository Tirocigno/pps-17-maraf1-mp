
package it.unibo.pps2017.core

import it.unibo.pps2017.core.deck.cards.Card

import scala.util.Random

package object deck {

  /**
    * Alias for a card hand.
    */
  type CardsHand = Iterable[Card]

  /**
    * Implicit class to randomize a Sequence of cards.
    *
    * @param cardSequence the card sequence to shuffle.
    */
  implicit class RandomizeCardSequence(var cardSequence: Seq[Card]) {
    def shuffle(): Unit = cardSequence = Random.shuffle(cardSequence)
  }
}
