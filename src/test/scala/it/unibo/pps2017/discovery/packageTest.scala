
package it.unibo.pps2017.discovery

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class packageTest extends FunSuite {

  test("ServerContexts equals") {
    val baseContext:ServerContext = ("a",0)
    val otherContext:ServerContext = ("a",0)
    assert(baseContext.equals(otherContext))
  }

  test("ServerContexts different ") {
    val baseContext:ServerContext = ("b",0)
    val otherContext:ServerContext = ("b",1)
    assert(!baseContext.equals(otherContext))
  }
}
