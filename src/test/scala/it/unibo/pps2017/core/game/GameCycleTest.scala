
package it.unibo.pps2017.core.game

<<<<<<< HEAD
import it.unibo.pps2017.core.player.Player
=======
import it.unibo.pps2017.core.player.{Player}
>>>>>>> feature/gameActor
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable.ListBuffer

@RunWith(classOf[JUnitRunner])
class GameCycleTest extends FunSuite {

  /**
    * Checking players turning.

  test("gameTurning") {
    val team1 = Team("Team1", ListBuffer())
<<<<<<< HEAD
    val team2 = Team("Team2", ListBuffer(generateTestPlayer("3"), generateTestPlayer("4")))
=======
   // val team2 = Team("Team2", ListBuffer(PlayerImpl("3"), PlayerImpl("4")))
>>>>>>> feature/gameActor

    assertThrows[TeamNotReadyException] {
      GameCycle(team1, team2)
    }

    team1.addPlayer(generateTestPlayer("1"))
    team1.addPlayer(generateTestPlayer("2"))
    val cycle = GameCycle(team1, team2)
    val queue: Seq[Player] = cycle.queue

    cycle.setFirst(queue(1))

    println(queue)

    assert(cycle.next() == queue(2))
    assert(cycle.next() == queue(3))
    assert(cycle.next() == queue.head)
    assert(cycle.next() == queue(1))
    assert(cycle.next() == queue(2))

    assert(cycle.getCurrent == queue(2))
    assert(cycle.getNext == queue(3))
  }*/



}
