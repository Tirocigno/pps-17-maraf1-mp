
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class ScoreCounterTest extends FunSuite with BeforeAndAfter {

  var scoreCounter: ScoreCounter = ScoreCounter()
  private[this] val highCardWithValue = 8
  private[this] val lowCardWithValue = 2
  private[this] val nullValueCard: Int = 5

  private[this] def generateCard(cardValue: Int): Card = CardImpl(Seed.Club, cardValue)

  before {
    scoreCounter = ScoreCounter()
  }

  test("Creation of a ScoreCounter object") {
    assert(scoreCounter.scores == (0, 0))
  }

  test("Register a card with no value") {
    scoreCounter.registerPlayedCard(generateCard(nullValueCard), FirstTeam)
    assert(scoreCounter.scores == (0, 0))
  }

  test("Register a ace") {
    scoreCounter.registerPlayedCard(generateCard(aceValue), FirstTeam)
    assert(scoreCounter.scores == (1, 0))
  }

  test("Register three low cards with values") {
    for (_ <- 0 to 3) scoreCounter.registerPlayedCard(generateCard(lowCardWithValue), FirstTeam)
    assert(scoreCounter.scores == (1, 0))
  }

  test("Register three high cards with values") {
    for (_ <- 0 to 3) scoreCounter.registerPlayedCard(generateCard(highCardWithValue), FirstTeam)
    assert(scoreCounter.scores == (1, 0))
  }

  test("Finish a set") {
    scoreCounter.registerPlayedCard(generateCard(aceValue), FirstTeam)
    scoreCounter.finishSet()
    assert(scoreCounter.scores == (2, 0))
  }

}
