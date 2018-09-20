
package it.unibo.pps2017.core.deck

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ScoreCounterTest extends FunSuite {

  test("Creation of a ScoreCounter object") {
    val scoreCounter: ScoreCounter = ScoreCounter()
    assert(scoreCounter.scores == (0, 0))
  }

}
