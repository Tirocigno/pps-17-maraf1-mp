
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl}
import it.unibo.pps2017.core.game.{BaseTeam, firstTeamID, secondTeamID}

/**
  * This trait register the played cards and it increment the score of the team which earned them.
  */
sealed trait ScoreCounter {
  /**
    * Register the scores of all the cards played in a set.
    *
    * @param cardPlayedSeq the card sequence played in a set.
    * @param team          the index of team which earned the cards.
    */
  def registerSetPlayedCards(cardPlayedSeq: Seq[Card], team: BaseTeam[String]): Unit

  /**
    * When called, signal the closure of a set and retrieve the teams score.
    *
    * @return a pair containing the teams score.
    */
  def computeSetScore(): (Int, Int)

  /**
    * Register scores of Marafona to specified team.
    *
    * @param team the team to register Marafona scores to.
    */
  def registerMarafona(team: BaseTeam[String]): Unit
}

/**
  * Companion object for the ScoreCounter Trait.
  */
object ScoreCounter {
  def apply(): ScoreCounter = new ScoreCounterImpl(new ScoreTracker, new ScoreTracker)
}

/**
  * This private class is used to hide the score computation from the scoreCounter trait.
  */
private class ScoreTracker {

  var currentScore: Int = 0

  /**
    * Increase currentScore with the value of the card parameter.
    * The cards, which has value 2,3 or between 7 and 10, increase the score of one point, the ones with 1 as value
    * increase the score of three points; all the other cards doesn't increase the score.
    *
    * @param card the card to register.
    */
  def registerCardScore(card: Card): Unit = card match {
    case CardImpl(_, cardValue) if cardValue == aceValue => currentScore += aceScore
    case CardImpl(_, cardValue) if cardValue < lowerCardValue || cardValue > upperCardValue =>
      currentScore += defaultCardScore
    case _ =>
  }

  /**
    * This method approximates currentScoreValue to the smaller multiple of 3
    */
  def discardThirdOfFigures() = {
    currentScore -= (currentScore % 3)
  }
}

/**
  * Implementation of ScoreCounter trait.
  *
  * @param teamScores a pair of ScoreTracker object, one for each team in the game.
  */
private class ScoreCounterImpl(val teamScores: (ScoreTracker, ScoreTracker)) extends ScoreCounter {

  var lastSetWinner: Option[BaseTeam[String]] = None

  override def registerSetPlayedCards(cardPlayedSeq: Seq[Card], team: BaseTeam[String]): Unit =
    cardPlayedSeq foreach (registerPlayedCard(_, team))

  override def computeSetScore(): (Int, Int) = {
    finishSet()
    scores
  }


  /**
    * Finish a game and register an additional score to the team who won the last set.
    */
  def finishSet(): Unit = matchTeam((team, value: Int) => {
    team.discardThirdOfFigures()
    team.currentScore += value
  })(aceScore)

  /**
    * Register the score of a card earned by a team and set the team as last set winner.
    *
    * @param playedCard the card played.
    * @param team       the index of the team which earned the cards.
    */
  def registerPlayedCard(playedCard: Card, team: BaseTeam[String]): Unit = {
    updateLastSetWinner(team)
    registerCardScore(playedCard)
  }

  def updateLastSetWinner(setWinner: BaseTeam[String]): Unit = lastSetWinner = Option(setWinner)

  def registerCardScore(playedCard: Card): Unit =
    matchTeam((team, card: Card) => team registerCardScore card)(playedCard)

  /**
    * Get the both team's scores.
    *
    * @return a pair containing the scores as integer.
    */
  def scores: (Int, Int) = (teamScores._1.currentScore / 3, teamScores._2.currentScore / 3)

  override def registerMarafona(team: BaseTeam[String]): Unit =
    matchTeam((team, value: Int) => team.currentScore += value)(marafonaScore)

  private[this] def matchTeam[A](apply: (ScoreTracker, A) => Unit)(argument: A): Unit = lastSetWinner match {
    case Some(team) if team.teamIndex == firstTeamID => apply(teamScores._1, argument)
    case Some(team) if team.teamIndex == secondTeamID => apply(teamScores._2, argument)
    case _ =>
  }


}

