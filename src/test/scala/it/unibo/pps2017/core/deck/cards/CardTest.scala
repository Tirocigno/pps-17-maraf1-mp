
package it.unibo.pps2017.core.deck.cards

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class CardTest extends FunSuite {

  test("Default object creation") {
    val card: Option[Card] = Option(CardImpl(defaultSeed, defaultValue))
    assert(card isDefined)
  }
}
