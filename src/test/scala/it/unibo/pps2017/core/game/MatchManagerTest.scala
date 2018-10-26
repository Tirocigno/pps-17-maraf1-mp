
package it.unibo.pps2017.core.game


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MatchManagerTest extends FunSuite {

  /**
    * Test the add Player method.

  test("testAddPlayer") {
    val game = MatchManager()


    val firstTeamName = game.firstTeam().name


    game.addPlayer(generateTestPlayer("1"))
    assert(game.getPlayers.length == 1)

    val notFoundedTeamName = "TEST"
    game.addPlayer(generateTestPlayer("2"), notFoundedTeamName)

    game.addPlayer(generateTestPlayer("3"), firstTeamName)
    assert(game.getPlayers.length == 2)
  }


  /**
    * Test the addPlayer method and check the if the team management work good.
    */
  test("testAddMoreOfTwoPlayerToATeam") {
    val game = MatchManager()

    val firstTeamName = game.firstTeam().name

    game.addPlayer(generateTestPlayer("1"), firstTeamName)
    game.addPlayer(generateTestPlayer("2"), firstTeamName)

    assert(game.getPlayers.length == TEAM_MEMBERS_LIMIT)
    assert(game.firstTeam().numberOfMembers == TEAM_MEMBERS_LIMIT)

    game.addPlayer(generateTestPlayer("3"), firstTeamName)
    assert(game.firstTeam().numberOfMembers == TEAM_MEMBERS_LIMIT)
  }

  /**
    * Team class test
    */
  test("teamTest") {
    val team = Team("TeamOne")

    assert(team.numberOfMembers == 0)

    team.addPlayer(generateTestPlayer("1"))
    team.addPlayer(generateTestPlayer("2"))

    assert(team.numberOfMembers == TEAM_MEMBERS_LIMIT)

    assertThrows[FullTeamException] {
      team.addPlayer(generateTestPlayer("3"))
    }

    assert(team.numberOfMembers == TEAM_MEMBERS_LIMIT)

  }

  /**
    * Checking the correctness of isFull method in the Team class.
    */
  test("TestFullTeam") {
    val team = Team("TeamOne")
    team.addPlayer(generateTestPlayer("1"))

    assert(!team.isFull)

    team.addPlayer(generateTestPlayer("2"))

    assert(team.isFull)
  }

  /*
  //TODO
  test("gameStartingTest") {
    val player1 = generateTestPlayer("1")
    val player2 = generateTestPlayer("2")
    val team1 = Team("TeamOne")
    team1.addPlayer(player1)
    team1.addPlayer(player2)

    val player3 = generateTestPlayer("3")
    val player4 = generateTestPlayer("4")
    val team2 = Team("TeamTwo")
    team2.addPlayer(player3)
    team2.addPlayer(player4)

    val game = MatchManager(team1, team2)


    assert(player1.getHand().size == MAX_HAND_CARDS)
    assert(player2.getHand().size == MAX_HAND_CARDS)
    assert(player3.getHand().size == MAX_HAND_CARDS)
    assert(player4.getHand().size == MAX_HAND_CARDS)

  }*/


  test("handTakerTestWithDifferentSuitAndBriscola") {
    val game = MatchManager()

    val player1 = generateTestPlayer("1")
    val player2 = generateTestPlayer("2")
    val player3 = generateTestPlayer("3")
    val player4 = generateTestPlayer("4")

    game.currentSuit = Option(Coin)
    game.currentBriscola = Option(Cup)

    println(player1, player2, player3, player4)

    var cards: mutable.ListBuffer[(Card, Player)] = mutable.ListBuffer(CardImpl(Coin, 8) -> player1, CardImpl(Coin, 7) -> player2,
      CardImpl(Club, 1) -> player3, CardImpl(Coin, 6) -> player4)

    assert(game.defineTaker(cards) == player1)

    cards = mutable.ListBuffer(CardImpl(Coin, 10) -> player1, CardImpl(Coin, 9) -> player2,
        CardImpl(Club, 1) -> player3, CardImpl(Coin, 2) -> player4)

    assert(game.defineTaker(cards) == player4)

    cards = mutable.ListBuffer(CardImpl(Coin, 10) -> player1, CardImpl(Coin, 1) -> player2,
      CardImpl(Club, 1) -> player3, CardImpl(Coin, 6) -> player4)

    assert(game.defineTaker(cards) == player2)

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

  test("PlayAcceptableRandomCard") {
    val game = MatchManager()

    val player = generateTestPlayer("1")
    game.addPlayer(player)


    player.setHand(Set(CardImpl(Coin, 1), CardImpl(Coin, 2), CardImpl(Cup, 5), CardImpl(Sword, 6), CardImpl(Sword, 3), CardImpl(Sword, 2)))

    game.currentSuit = Option(Coin)

    val acceptableCards: Set[Card] = Set(CardImpl(Coin, 1), CardImpl(Coin, 2))

    assert(acceptableCards.contains(game.forcePlay(player)))
  }*/
}
