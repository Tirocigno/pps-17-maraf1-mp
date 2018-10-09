
package it.unibo.pps2017.discovery.structures

import it.unibo.pps2017.discovery
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class ServerMapTest extends FunSuite with BeforeAndAfterEach {

  val mockIP = "0.0.0.0"
  val mockIP2 = "0.0.1.0"
  val mockPort = 4851
  val mockServerContext = discovery.ServerContext(mockIP, mockPort)
  val otherServerContext = discovery.ServerContext(mockIP, mockPort)
  var serverMap: ServerMap = ServerMap()

  override def beforeEach() {
    serverMap = ServerMap()
  }

  test("Register a new Server") {
    serverMap.addServer(mockServerContext)
    assert(serverMap.getLessBusyServer.equals(mockServerContext))
  }
}
