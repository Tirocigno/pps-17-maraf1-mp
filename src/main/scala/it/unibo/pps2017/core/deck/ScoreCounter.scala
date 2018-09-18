
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.Card

/**
  * This trait register the played cards and it increment the score of the team which earned them.
  */
sealed trait ScoreCounter {

  def scores: (Int, Int)

  def registerPlayedCard(playedCard: Card, player: Int)

  def startNewSet(): Unit
}

