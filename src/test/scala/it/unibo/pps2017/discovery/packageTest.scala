
package it.unibo.pps2017.discovery

import it.unibo.pps2017.commons.remote.rest.RestUtils.ServerContext
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class packageTest extends FunSuite {

  test("ServerContexts equals") {
    val baseContext: ServerContext = ServerContext("a", 0)
    val otherContext: ServerContext = ServerContext("a", 0)
    assert(baseContext.equals(otherContext))
  }

  test("ServerContexts different ") {
    val baseContext: ServerContext = ServerContext("b", 0)
    val otherContext: ServerContext = ServerContext("b", 1)
    assert(!baseContext.equals(otherContext))
  }
}
