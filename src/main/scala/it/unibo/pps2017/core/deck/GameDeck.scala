
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.Card

trait GameDeck {

  /**
    * This method is called before a set to shuffle the cards inside the deck.
    */
  def shuffle(): Unit

  /**
    * Splits the cards inside the deck into four hand of cards.
    *
    * @return a list containing the four hands as collections.
    */
  def distribute: Seq[Iterable[Card]]

  /**
    * This method signal the deck the set is over, compute the new score and it return it.
    *
    * @return a Scala Tuple containing two integers, the values of the team's current scores.
    */
  def computeSetScore: (Int, Int)

  /**
    * Register in the score storage all the card played in a turn.
    *
    * @param playedCards a Collection containing the four card played in a turn.
    * @param teamIndex   the index of the team which earned those cards.
    */
  def registerTurnPlayedCards(playedCards: Seq[Card], teamIndex: Int): Unit
}
