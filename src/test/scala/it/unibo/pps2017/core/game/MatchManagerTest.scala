package it.unibo.pps2017.core.game

import java.util

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.deck.cards.Seed.{Club, Coin, Cup}
import it.unibo.pps2017.core.game.MatchManager.{MAX_HAND_CARDS, TEAM_MEMBERS_LIMIT}
import it.unibo.pps2017.core.player.Controller
import org.scalatest.FunSuite

import scala.collection.mutable
import scala.util.Random

class MatchManagerTest extends FunSuite {

  /**
    * Test the add Player method.
    */
  test("testAddPlayer") {
    val game = MatchManager()


    val firstTeamName = game.firstTeam().name


    game.addPlayer(Player())
    assert(game.getPlayers.length == 1)

    val notFoundedTeamName = "TEST"
    game.addPlayer(Player(), notFoundedTeamName)

    game.addPlayer(Player(), firstTeamName)
    assert(game.getPlayers.length == 2)
  }


  /**
    * Test the addPlayer method and check the if the team management work good.
    */
  test("testAddMoreOfTwoPlayerToATeam") {
    val game = MatchManager()

    val firstTeamName = game.firstTeam().name

    game.addPlayer(Player(), firstTeamName)
    game.addPlayer(Player(), firstTeamName)

    assert(game.getPlayers.length == TEAM_MEMBERS_LIMIT)
    assert(game.firstTeam().numberOfMembers == TEAM_MEMBERS_LIMIT)

    game.addPlayer(Player(), firstTeamName)
    assert(game.firstTeam().numberOfMembers == TEAM_MEMBERS_LIMIT)
  }

  /**
    * Team class test
    */
  test("teamTest") {
    val team = Team("TeamOne")

    assert(team.numberOfMembers == 0)

    team.addPlayer(Player())
    team.addPlayer(Player())

    assert(team.numberOfMembers == TEAM_MEMBERS_LIMIT)

    assertThrows[FullTeamException] {
      team.addPlayer(Player())
    }

    assert(team.numberOfMembers == TEAM_MEMBERS_LIMIT)

  }

  /**
    * Checking the correctness of isFull method in the Team class.
    */
  test("TestFullTeam") {
    val team = Team("TeamOne")
    team.addPlayer(Player())

    assert(!team.isFull)

    team.addPlayer(Player())

    assert(team.isFull)
  }


  test("gameStartingTest") {
    val player1 = Player()
    val player2 = Player()
    val team1 = Team("TeamOne")
    team1.addPlayer(player1)
    team1.addPlayer(player2)

    val player3 = Player()
    val player4 = Player()
    val team2 = Team("TeamTwo")
    team2.addPlayer(player3)
    team2.addPlayer(player4)

    val game = MatchManager(team1, team2)


    assert(player1.hand.size() == MAX_HAND_CARDS)
    assert(player2.hand.size() == MAX_HAND_CARDS)
    assert(player3.hand.size() == MAX_HAND_CARDS)
    assert(player4.hand.size() == MAX_HAND_CARDS)

  }


  test("handTakerTestWithDifferentSuitAndBriscola") {
    val game = MatchManager()

    val player1 = Player()
    val player2 = Player()
    val player3 = Player()
    val player4 = Player()

    game.currentSuit = Coin
    game.currentBriscola = Cup

    println(player1, player2, player3, player4)

    var cards: mutable.ListBuffer[(Card, Controller)] = mutable.ListBuffer(CardImpl(Coin, 8) -> player1, CardImpl(Coin, 7) -> player2,
      CardImpl(Club, 1) -> player3, CardImpl(Coin, 6) -> player4)

    assert(game.defineTaker(cards) == player1)

    cards = mutable.ListBuffer(CardImpl(Coin, 10) -> player1, CardImpl(Coin, 10) -> player2,
        CardImpl(Club, 1) -> player3, CardImpl(Coin, 2) -> player4)

    assert(game.defineTaker(cards) == player4)

    //TODO Capire il lancio dell'eccezione sul 4 di denara
    //cards = mutable.Map(CardImpl(Coin, 10) -> player1, CardImpl(Coin, 4) -> player2,
     // CardImpl(Club, 1) -> player3, CardImpl(Coin, 6) -> player4)

    //assert(game.defineTaker(cards) == player2)

    cards = mutable.ListBuffer(CardImpl(Coin, 7) -> player1, CardImpl(Coin, 5) -> player2,
      CardImpl(Club, 4) -> player3, CardImpl(Cup, 6) -> player4)

    assert(game.defineTaker(cards) == player4)

    cards = mutable.ListBuffer(CardImpl(Coin, 1) -> player1, CardImpl(Cup, 5) -> player2,
      CardImpl(Cup, 1) -> player3, CardImpl(Cup, 10) -> player4)

    assert(game.defineTaker(cards) == player3)

    cards = mutable.ListBuffer(CardImpl(Cup, 2) -> player1, CardImpl(Cup, 1) -> player2,
      CardImpl(Cup, 3) -> player3, CardImpl(Coin, 3) -> player4)

    assert(game.defineTaker(cards) == player3)

    cards = mutable.ListBuffer(CardImpl(Cup, 8) -> player1, CardImpl(Cup, 5) -> player2,
      CardImpl(Cup, 6) -> player3, CardImpl(Club, 3) -> player4)

    assert(game.defineTaker(cards) == player1)
  }
}

case class Player(name: String = "Random" + Random.nextInt(1000)) extends Controller {
  var hand: util.Set[Card] = _

  override def getHand: util.Set[Card] = hand

  override def setHand(hand: util.Set[Card]): Unit = this.hand = hand
}
