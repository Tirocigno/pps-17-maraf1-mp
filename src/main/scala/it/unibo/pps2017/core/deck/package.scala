
package it.unibo.pps2017.core

import it.unibo.pps2017.core.deck.cards.Card

package object deck {

  val maxCardRangeValue: Int = 11
  val minCardRangeValue: Int = 1
  val defaultDeckSize: Int = 40
  val handSize: Int = 10
  val expectedHandsNumber: Int = 4

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

  object SeqExtractor {
    def unapply[A](seq: Seq[A]): Option[(A, Seq[A])] = if (seq.nonEmpty) {
      Some((seq.head, seq.tail))
    }
    else {
      None
    }
  }

}
