
package it.unibo.pps2017.core.deck

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SimpleDeckTest extends FunSuite {

  /**
    * Test to check the size of the default deck.
    */
  test("Size of Default deck") {
    assert(SimpleDeck.generateDefaultCardsList().size == defaultDeckSize)
  }
}
