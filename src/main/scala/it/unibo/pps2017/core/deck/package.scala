
package it.unibo.pps2017.core

import it.unibo.pps2017.core.deck.cards.Card

package object deck {

  val maxCardRangeValue: Int = 11
  val minCardRangeValue: Int = 1
  val defaultDeckSize: Int = 40;

  /**
    * Alias for a card hand.
    */
  type CardsHand = Iterable[Card]

  /**
    * Implicit class to randomize a Sequence of cards.
    *
    * @param cardSequence the card sequence to shuffle.
    */
  implicit class RichCardSequence(var cardSequence: Seq[Card]) {

    def compareSequence(otherSequence: Seq[Card])(compare: (Seq[Card], Seq[Card]) => Boolean): Boolean =
      compare(cardSequence, otherSequence)
  }

}
