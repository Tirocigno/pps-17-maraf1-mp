package it.unibo.pps2017.core.game

import java.util

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl}
import it.unibo.pps2017.core.deck.cards.Seed.{Coin, Sword}
import it.unibo.pps2017.core.player.Controller
import org.scalatest.FunSuite

import scala.util.Random

class MatchManagerTest extends FunSuite {

  /**
    * Test the add Player method.
    */
  test("testAddPlayer") {
    val game = MatchManager()

    val firstTeamName = game.team1.name


    game.addPlayer(Player())
    assert(game.players.length == 1)

    val notFoundedTeamName = "TEST"
    game.addPlayer(Player(), notFoundedTeamName)

    game.addPlayer(Player(), firstTeamName)
    assert(game.players.length == 2)
  }


  /**
    * Test the addPlayer method and check the if the team management work good.
    */
  test("testAddMoreOfTwoPlayerToATeam") {
    val game = MatchManager()

    val firstTeamName = game.team1.name

    game.addPlayer(Player(), firstTeamName)
    game.addPlayer(Player(), firstTeamName)

    assert(game.players.length == MatchManager.TEAM_MEMBERS_LIMIT)
    assert(game.team1.numberOfMembers == MatchManager.TEAM_MEMBERS_LIMIT)

    game.addPlayer(Player(), firstTeamName)
    assert(game.team1.numberOfMembers == MatchManager.TEAM_MEMBERS_LIMIT)
  }

  /**
    * Team class test
    */
  test("teamTest") {
    val team = Team("TeamOne")

    assert(team.numberOfMembers == 0)

    team.addPlayer(Player())
    team.addPlayer(Player())

    assert(team.numberOfMembers == MatchManager.TEAM_MEMBERS_LIMIT)

    assertThrows[FullTeamException] {
      team.addPlayer(Player())
    }

    assert(team.numberOfMembers == MatchManager.TEAM_MEMBERS_LIMIT)

  }

}

case class Player(name: String = "Random" + Random.nextInt(1000)) extends Controller {
  override def getHand: util.Set[Card] = new util.HashSet[Card](util.Arrays.asList(CardImpl(Sword, 2), CardImpl(Coin, 4)))
}
