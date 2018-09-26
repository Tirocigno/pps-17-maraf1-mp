package it.unibo.pps2017.core.game

import it.unibo.pps2017.core.player.Controller
import org.scalatest.FunSuite

import scala.collection.mutable.ListBuffer

class GameCycleTest extends FunSuite {

  /**
    * Checking players turning.
    */
  test("gameTurning") {
    val team1 = Team("Team1", ListBuffer())
    val team2 = Team("Team2", ListBuffer(Player(), Player()))

    assertThrows[TeamNotReadyException] {
      GameCycle(team1, team2)
    }

    team1.addPlayer(Player())
    team1.addPlayer(Player())
    val cycle = GameCycle(team1, team2)
    val queue: Seq[Controller] = cycle.queue

    cycle.setFirst(queue(1))

    println(queue)

    assert(cycle.next() == queue(1))
    assert(cycle.next() == queue(2))
    assert(cycle.next() == queue(3))
    assert(cycle.next() == queue.head)
    assert(cycle.next() == queue(1))

    assert(cycle.getCurrent == queue(2))
    assert(cycle.getNext == queue(3))
  }
}
