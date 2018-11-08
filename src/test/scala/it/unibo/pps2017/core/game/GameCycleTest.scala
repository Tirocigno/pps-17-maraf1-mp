
package it.unibo.pps2017.core.game

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.collection.mutable.ListBuffer

@RunWith(classOf[JUnitRunner])
class GameCycleTest extends FunSuite {


  //   Checking players turning.
    
     test("gameTurning") {
    val team1 = Team("Team1")
     val team2 = Team("Team2", ListBuffer("3", "4"))
     
    
     assertThrows[TeamNotReadyException] {
    GameCycle(team1, team2)
     }
    
    team1.addPlayer("Player1")
    team1.addPlayer("Player2")
    val cycle = GameCycle(team1, team2)
    val queue: Seq[String] = cycle.queue
    
 cycle.setFirst(queue(1))
    
 println(queue)
    
 assert(cycle.next() == queue(2))
    assert(cycle.next() == queue(3))
    assert(cycle.next() == queue.head)
    assert(cycle.next() == queue(1))
    assert(cycle.next() == queue(2))
    
 assert(cycle.getCurrent == queue(2))
    assert(cycle.getNext == queue(3))
  }



}
