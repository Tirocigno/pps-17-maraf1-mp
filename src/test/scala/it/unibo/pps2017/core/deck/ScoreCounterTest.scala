
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.game.{Team, firstTeamID}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}


@RunWith(classOf[JUnitRunner])
class ScoreCounterTest extends FunSuite with BeforeAndAfter {

  var scoreCounter: ScoreCounter = ScoreCounter()
  private[this] val highCardWithValue = 8
  private[this] val lowCardWithValue = 2
  private[this] val noCardValue: Int = 5

  private[this] def generateCard(cardValue: Int): Card = CardImpl(Seed.Club, cardValue)

  private def generateOneScoreHand =
    Seq(generateCard(lowCardWithValue), generateCard(highCardWithValue),
      generateCard(noCardValue), generateCard(lowCardWithValue))

  private def generateNoScoreHand =
    Stream.range(0, handSize).map(_ => generateCard(noCardValue))

  private def generateMixedHand = Seq(generateCard(aceValue), generateCard(lowCardWithValue),
    generateCard(highCardWithValue), generateCard(noCardValue))

  private def registerHandAndCheckScore(handToRegister: Seq[Card])(expectedScore: Int) = {
    val team: Team = Team(firstTeamID)
    scoreCounter.registerSetPlayedCards(handToRegister, team)
    assert(scoreCounter.computeSetScore() == (expectedScore + 1, 0))
  }

  before {
    scoreCounter = ScoreCounter()
  }

  test("Initial test status") {
    assert(scoreCounter.computeSetScore() == (0, 0))
  }

  test("Register one score hand with figures") {
    registerHandAndCheckScore(generateOneScoreHand)(1)
  }

  test("Register zero score hand") {
    registerHandAndCheckScore(generateNoScoreHand)(0)
  }

  test("Register mixed hand") {
    registerHandAndCheckScore(generateMixedHand)(1)
  }



}
