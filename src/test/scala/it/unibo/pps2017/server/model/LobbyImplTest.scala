package it.unibo.pps2017.server.model

import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.server.model.LobbyStatusResponse.{FULL, OK, REVERSE}
import org.scalatest.FunSuite

class LobbyImplTest extends FunSuite {

  test("testCanContains") {
    val lobby = LobbyImpl(_ => {})

    val genericPlayer = "Player1"
    val team1: SimpleTeam = SimpleTeam("FIRST")
    team1.addPlayer("Player2")
    team1.addPlayer("Player3")

    val team2: SimpleTeam = SimpleTeam("SECOND")

    team2.addPlayer("Player4")
    team2.addPlayer("Player5")


    lobby.addPlayer(genericPlayer)

    assert(lobby.canContains(team1) == OK)
    assert(lobby.canContains(team2) == OK)

    assert(lobby.canContains(team1, Some(team2)) == FULL)

    val singlePlayerTeam: SimpleTeam = SimpleTeam("FIRST")
      singlePlayerTeam.addPlayer(genericPlayer)

    assert(lobby.canContains(singlePlayerTeam, Some(team2)) == OK)
    assert(lobby.canContains(team2, Some(singlePlayerTeam)) == REVERSE)
  }

}
