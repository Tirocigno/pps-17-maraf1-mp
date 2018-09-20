
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class ScoreCounterTest extends FunSuite with BeforeAndAfter {

  var scoreCounter: ScoreCounter = ScoreCounter()

  private[this] def generateCard(cardValue: Int): Card = new CardImpl(Seed.Club, cardValue)

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

}
