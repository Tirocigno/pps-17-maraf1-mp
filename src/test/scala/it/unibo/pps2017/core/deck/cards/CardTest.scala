
package it.unibo.pps2017.core.deck.cards

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class CardTest extends FunSuite {

  test("Default object creation") {
    val card: Option[Card] = Option(CardImpl(DEFAULT_SEED, MINOR_VALUE))
    assert(card isDefined)
  }

  test("Test for the fields") {
    val card = CardImpl(DEFAULT_SEED, MINOR_VALUE)
    assert(card.cardSeed == DEFAULT_SEED &&
      card.cardValue == MINOR_VALUE)
  }

  test("Test greater functionality") {
    val greatCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    val lowCard = CardImpl(DEFAULT_SEED, MINOR_VALUE)
    assert(greatCard > lowCard)
  }

  test("Test lower functionality") {
    val greatCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    val lowCard = CardImpl(DEFAULT_SEED, MINOR_VALUE)
    assert(lowCard < greatCard)
  }

  test("Test equals") {
    val baseCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    val otherCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    assert(baseCard equals otherCard)
  }

  test("Test equals with different value") {
    val baseCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    val otherCard = CardImpl(DEFAULT_SEED, MINOR_VALUE)
    assert(!(baseCard equals otherCard))
  }

  test("Test equals with different seed") {
    val baseCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    val otherCard = CardImpl(OTHER_SEED, MINOR_VALUE)
    assert(!(baseCard equals otherCard))
  }

  test("Switch with MajorValue card compare major") {
    val baseCard = CardImpl(DEFAULT_SEED, SWITCH_VALUE)
    val otherCard = CardImpl(DEFAULT_SEED, MAJOR_VALUE)
    assert(otherCard > baseCard && baseCard < otherCard)
  }


  test("Switch with MinorValue card compare") {
    val baseCard = CardImpl(DEFAULT_SEED, SWITCH_VALUE)
    val otherCard = CardImpl(DEFAULT_SEED, MINOR_VALUE)
    assert(otherCard > baseCard && baseCard < otherCard)
  }


}
