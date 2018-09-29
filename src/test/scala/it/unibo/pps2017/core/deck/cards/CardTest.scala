
package it.unibo.pps2017.core.deck.cards

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class CardTest extends FunSuite {

  val switchValue = 4

  test("Default object creation") {
    val card: Option[Card] = Option(CardImpl(defaultSeed, minorValue))
    assert(card isDefined)
  }

  test("Test for the fields") {
    val card = CardImpl(defaultSeed, minorValue)
    assert(card.cardSeed == defaultSeed &&
      card.cardValue == minorValue)
  }

  test("Test greater functionality") {
    val greatCard = CardImpl(defaultSeed, majorValue)
    val lowCard = CardImpl(defaultSeed, minorValue)
    assert(greatCard > lowCard)
  }

  test("Test lower functionality") {
    val greatCard = CardImpl(defaultSeed, majorValue)
    val lowCard = CardImpl(defaultSeed, minorValue)
    assert(lowCard < greatCard)
  }

  test("Test equals") {
    val baseCard = CardImpl(defaultSeed, majorValue)
    val otherCard = CardImpl(defaultSeed, majorValue)
    assert(baseCard equals otherCard)
  }

  test("Test equals with different value") {
    val baseCard = CardImpl(defaultSeed, majorValue)
    val otherCard = CardImpl(defaultSeed, minorValue)
    assert(!(baseCard equals otherCard))
  }

  test("Test equals with different seed") {
    val baseCard = CardImpl(defaultSeed, majorValue)
    val otherCard = CardImpl(otherSeed, minorValue)
    assert(!(baseCard equals otherCard))
  }

  test("Test greater with same card") {
    val baseCard = CardImpl(defaultSeed, majorValue)
    assert(!(baseCard > baseCard))
  }

  test("Test lower with same card") {
    val baseCard = CardImpl(defaultSeed, majorValue)
    assert(!(baseCard < baseCard))
  }

  test("Test greater with same card and a no use value") {
    val baseCard = CardImpl(defaultSeed, switchValue)
    assert(!(baseCard > baseCard))
  }

  test("Test lower functionality with switch value") {
    val greatCard = CardImpl(defaultSeed, minorValue)
    val lowCard = CardImpl(defaultSeed, switchValue)
    assert(lowCard < greatCard)
  }

  test("Test greater functionality with switch value") {
    val greatCard = CardImpl(defaultSeed, majorValue)
    val lowCard = CardImpl(defaultSeed, switchValue)
    assert(lowCard < greatCard)
  }

}
