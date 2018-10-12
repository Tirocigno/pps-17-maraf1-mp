
package it.unibo.pps2017.discovery.structures

import it.unibo.pps2017.discovery.MatchRef
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class MatchesSetTest extends FunSuite with BeforeAndAfterEach {

  var setTest:MatchesSet = MatchesSet()
  val matchRef:MatchRef = "Match1"

  override def beforeEach() {
    setTest = MatchesSet()
  }

  test("Match generation") {
    assert(setTest.getAllMatches isEmpty)
  }

  test("Adding a testRef") {
    setTest.addMatch(matchRef)
    assert(setTest.getAllMatches.size == 1)
  }

  test("Removing a testRef") {
    setTest.addMatch(matchRef)
    setTest.removeMatch(matchRef)
    assert(setTest.getAllMatches isEmpty)
  }

  test("Defensive copy in getAllMatches") {
    setTest.addMatch(matchRef)
    val tempSet = setTest.getAllMatches
    setTest.removeMatch(matchRef)
    assert(tempSet.size == 1)
  }
}
