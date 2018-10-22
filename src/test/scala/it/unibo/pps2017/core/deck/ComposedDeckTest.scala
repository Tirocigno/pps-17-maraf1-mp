package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.game.SimpleTeam
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ComposedDeckTest extends FunSuite {

  val gameDeck: GameDeck = ComposedDeck()
  val SCORES_SUM: Int = 11
  val MARAFONA_SCORES: Int = 3

  val team1 = SimpleTeam("Team1")
  val team2 = SimpleTeam("Team2")

  /**
    * Simulate a single marafone match.
    *
    * @return the scores at the end of all the turns.
    */
  private def playSingleMatch() = {
    gameDeck.shuffle()
    val hands = gameDeck.distribute()
    for (i <- 0 to 9) {
      val turnPlayedCards = hands
        .map(_.toList)
        .map(_ (i))
      if (i % 2 == 0) {
        gameDeck.registerTurnPlayedCards(turnPlayedCards, team1)
      } else {
        gameDeck.registerTurnPlayedCards(turnPlayedCards, team2)
      }
    }
    gameDeck.computeSetScore()
  }

  test("Play a single match") {
    val gameResult = playSingleMatch()
    assert((gameResult._1 + gameResult._2) % SCORES_SUM == 0)
  }

  test("play multiple matches") {
    for (i <- 0 to 100) {
      val gameResult = playSingleMatch()
      assert((gameResult._1 + gameResult._2) % SCORES_SUM == 0)
    }
  }

  test("play multiple matches with marafona") {
    gameDeck.registerMarafona(team1)
    for (i <- 0 to 100) {
      val gameResult = playSingleMatch()
      assert((gameResult._1 + gameResult._2) % SCORES_SUM == MARAFONA_SCORES)
    }
  }

}
