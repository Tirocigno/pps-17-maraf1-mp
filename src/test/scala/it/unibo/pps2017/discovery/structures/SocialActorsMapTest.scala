package it.unibo.pps2017.discovery.structures

import akka.actor.ActorRef
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, FunSuite}

@RunWith(classOf[JUnitRunner])
class SocialActorsMapTest extends FunSuite with BeforeAndAfterEach {

  private val demoActorRef = ActorRef.noSender
  private val demoID = "DemoID"
  private var socialActorsMap: SocialActorsMap = _

  override def beforeEach() {
    socialActorsMap = SocialActorsMap()
  }

  test("Adding a new actor to map") {
    socialActorsMap.registerUser(demoID, demoActorRef)
    assert(socialActorsMap.getCurrentOnlinePlayerMap.nonEmpty)
  }

  test("Removing actor from map") {
    socialActorsMap.registerUser(demoID, demoActorRef)
    socialActorsMap.unregisterUser(demoID)
    assert(socialActorsMap.getCurrentOnlinePlayerMap.isEmpty)
  }

}
