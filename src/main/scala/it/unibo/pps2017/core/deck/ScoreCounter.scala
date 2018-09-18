
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl}

/**
  * This trait register the played cards and it increment the score of the team which earned them.
  */
sealed trait ScoreCounter {

  def scores: (Int, Int)

  def registerPlayedCard(playedCard: Card, player: Int): Unit

  def startNewSet(): Unit
}

private class ScoreTracker {
  var currentScore: Int = 0

  def registerCardScore(card: Card): Unit = card match {
    case CardImpl(_, cardValue) if cardValue == 1 => currentScore += 3
    case CardImpl(_, cardValue) if cardValue < 3 || cardValue > 7 => currentScore += 1
  }
}

class ScoreCounterImpl extends ScoreCounter {
  private val teamScores: (ScoreTracker, ScoreTracker) = (new ScoreTracker, new ScoreTracker)

  override def scores: (Int, Int) = (teamScores._1.currentScore, teamScores._2.currentScore)

  override def registerPlayedCard(playedCard: Card, player: Int): Unit = player match {
    case 0 => teamScores._1.registerCardScore(playedCard)
    case 1 => teamScores._2.registerCardScore(playedCard)
  }

  override def startNewSet(): Unit = ???

}

