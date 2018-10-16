
package it.unibo.pps2017.discovery.structures

import it.unibo.pps2017.commons.remote.RestUtils
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class ServerMapTest extends FunSuite with BeforeAndAfterEach {

  val mockIP = "0.0.0.0"
  val mockIP2 = "0.0.1.0"
  val mockPort = 4851
  val mockServerContext = RestUtils.ServerContext(mockIP, mockPort)
  val otherServerContext = RestUtils.ServerContext(mockIP2, mockPort)
  var serverMap: ServerMap = ServerMap()

  override def beforeEach() {
    serverMap = ServerMap()
  }

  test("Register a new Server") {
    serverMap.addServer(mockServerContext)
    assert(serverMap.getLessBusyServer.get.equals(mockServerContext))
  }

  test("Removing a registered server") {
    serverMap.addServer(mockServerContext)
    serverMap.removeServer(mockServerContext)
    assert(serverMap.getLessBusyServer.isEmpty)
  }


  test("Get actual less busy server") {
    val secondMapContext = RestUtils.ServerContext(mockIP2, mockPort)
    serverMap.addServer(mockServerContext)
    serverMap.addServer(otherServerContext)
    try {
      serverMap.increaseMatchesPlayedOnServer(otherServerContext)
      serverMap.increaseMatchesPlayedOnServer(otherServerContext)
      serverMap.increaseMatchesPlayedOnServer(mockServerContext)
      assert(serverMap.getLessBusyServer.get.equals(mockServerContext))
    } catch {
      case e: IllegalArgumentException => fail(e)
    }
  }

  test("Get a server if no server is registered.") {
    assert(serverMap.getLessBusyServer.isEmpty)
  }

  test("Increase the number of matches on a registered server") {
    serverMap.addServer(mockServerContext)
    try {
      serverMap.increaseMatchesPlayedOnServer(mockServerContext)
    } catch {
      case _: IllegalArgumentException => fail()
    }
  }

  test("Increase the number of matches on a non registered server") {
    assertThrows[IllegalArgumentException](serverMap.increaseMatchesPlayedOnServer(mockServerContext))
  }

  test("Decrease the number of matches on a registered server") {
    serverMap.addServer(mockServerContext)
    try {
      serverMap.increaseMatchesPlayedOnServer(mockServerContext)
      serverMap.decreaseMatchesPlayedOnServer(mockServerContext)
    } catch {
      case e: IllegalArgumentException => fail(e)
      case e: IllegalStateException => fail(e)
    }
  }

  test("Decrease the number of matches on a server which hasn't any match") {
    serverMap.addServer(mockServerContext)
    assertThrows[IllegalStateException](serverMap.decreaseMatchesPlayedOnServer(mockServerContext))
  }

  test("Decrease the number of matches on a non registered server") {
    assertThrows[IllegalArgumentException](serverMap.decreaseMatchesPlayedOnServer(mockServerContext))
  }

}
