
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl}

/**
  * This trait register the played cards and it increment the score of the team which earned them.
  */
sealed trait ScoreCounter {

  /**
    * Get the both team's scores.
    *
    * @return a pair containing the scores as integer.
    */
  def scores: (Int, Int)

  /**
    * Register the score of a card earned by a team and set the team as last set winner.
    *
    * @param playedCard the card played.
    * @param player     the index of the team which earned the cards.
    */
  def registerPlayedCard(playedCard: Card, player: Int): Unit

  /**
    * Finish a game and register an additional score to the team who won the last set.
    */
  def finishSet(): Unit
}

/**
  * This private class is used to hide the score computation from the scoreCounter trait.
  * The card which has value 2,3 or betwee
  */
private class ScoreTracker {

  var currentScore: Int = 0

  def registerCardScore(card: Card): Unit = card match {
    case CardImpl(_, cardValue) if cardValue == aceValue => currentScore += aceScore
    case CardImpl(_, cardValue) if cardValue < lowerCardValue || cardValue > upperCardValue =>
      currentScore += defaultCardScore
  }
}

class ScoreCounterImpl(val teamScores: (ScoreTracker, ScoreTracker)) extends ScoreCounter {

  private[this] var lastSetWinner: Int = FirstTeam

  private[this] def matchTeam[A](argument: A)(apply: ScoreTracker => Unit): Unit = lastSetWinner match {
    case FirstTeam => apply(teamScores._1, argument)
    case FirstTeam => apply(teamScores._2, argument)
  }

  private[this] def updateLastSetWinner(setWinner: Int): Unit = lastSetWinner = setWinner

  private[this] def registerCardScore(playedCard: Card): Unit =
    matchTeam(playedCard)(_ registerCardScore playedCard)

  override def scores: (Int, Int) = (teamScores._1.currentScore / 3, teamScores._2.currentScore / 3)

  override def registerPlayedCard(playedCard: Card, player: Int): Unit = {
    updateLastSetWinner(player)
    registerCardScore(playedCard)
  }

  override def finishSet(): Unit = matchTeam()(_.currentScore += aceScore)

}

