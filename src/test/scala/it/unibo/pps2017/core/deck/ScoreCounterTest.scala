
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.game.{BaseTeam, SimpleTeam, firstTeamID}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}


@RunWith(classOf[JUnitRunner])
class ScoreCounterTest extends FunSuite with BeforeAndAfter {

  var scoreCounter: ScoreCounter = ScoreCounter()
  private[this] val HIGH_CARD_WITH_VALUE = 8
  private[this] val LOW_CARD_WITH_VALUE = 2
  private[this] val CARD_WITH_NO_VALUE: Int = 5

  private[this] def generateCard(cardValue: Int): Card = CardImpl(Seed.Club, cardValue)

  private def generateOneScoreCardSet =
    Seq(generateCard(LOW_CARD_WITH_VALUE), generateCard(HIGH_CARD_WITH_VALUE),
      generateCard(CARD_WITH_NO_VALUE), generateCard(LOW_CARD_WITH_VALUE))

  private def generateNoScoreCardSet =
    Stream.range(0, handSize).map(_ => generateCard(CARD_WITH_NO_VALUE))

  private def generateOneFigureZeroScoreCardSet =
    Seq(generateCard(LOW_CARD_WITH_VALUE), generateCard(CARD_WITH_NO_VALUE),
      generateCard(CARD_WITH_NO_VALUE), generateCard(CARD_WITH_NO_VALUE))


  private def generateMixedCardSet = Seq(generateCard(aceValue), generateCard(LOW_CARD_WITH_VALUE),
    generateCard(HIGH_CARD_WITH_VALUE), generateCard(CARD_WITH_NO_VALUE))

  private def registerCardSetAndCheckScore(handToRegister: Seq[Card])(expectedScore: Int) = {
    val team: BaseTeam[String] = SimpleTeam(firstTeamID)
    scoreCounter.registerSetPlayedCards(handToRegister, team)
    assert(scoreCounter.computeSetScore() == (expectedScore + 1, 0))
  }

  before {
    scoreCounter = ScoreCounter()
  }

  test("Initial test status") {
    assert(scoreCounter.computeSetScore() == (0, 0))
  }

  test("Register one score card set with figures") {
    registerCardSetAndCheckScore(generateOneScoreCardSet)(1)
  }

  test("Register zero score card set") {
    registerCardSetAndCheckScore(generateNoScoreCardSet)(0)
  }

  test("Register mixed card set") {
    registerCardSetAndCheckScore(generateMixedCardSet)(1)
  }

  test("Discard thirds of scores on set conclusion") {
    val playedCards = generateOneFigureZeroScoreCardSet
    for (i <- 0 to 3) registerCardSetAndCheckScore(playedCards)(i)

  }



}
