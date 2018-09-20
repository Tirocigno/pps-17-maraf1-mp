
package it.unibo.pps2017.core.deck.cards

import org.scalatest.FunSuite

import scala.language.postfixOps

//@RunWith(classOf[JUnitRunner])
class CardTest extends FunSuite {

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

}
