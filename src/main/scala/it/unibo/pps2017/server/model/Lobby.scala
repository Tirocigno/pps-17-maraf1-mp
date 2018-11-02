
package it.unibo.pps2017.server.model

import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.core.player.{FullTeamException, GameActor}
import it.unibo.pps2017.server.model.LobbyStatusResponse.{FULL, LobbyStatusResponse, OK, REVERSE}

sealed trait Lobby {
  /**
    * Game ID
    *
    * @return
    * The Game ID
    */
  def id: String

  /**
    * First team
    *
    * @return
    */
  def team1: SimpleTeam

  /**
    * Second team
    *
    * @return
    */
  def team2: SimpleTeam

  /**
    * Return TRUE if there are 4 players in the lobby,
    * FALSE otherwise.
    *
    * @return
    * TRUE if there are 4 players in the lobby,
    * FALSE otherwise
    */
  def isFull: Boolean

  /**
    * Try to add a player to the lobby.
    *
    * @param player
    * Player ID
    * @throws it.unibo.pps2017.server.model.FullLobbyException
    * If the lobby is full.
    */
  @throws(classOf[FullLobbyException])
  def addPlayer(player: String)

  /**
    * Try to add a team to the lobby.
    *
    * @param team
    * Simple team with player's IDs
    * @throws it.unibo.pps2017.server.model.FullLobbyException
    * If the lobby can't contain a new team.
    */
  @throws(classOf[FullLobbyException])
  def addTeam(team: SimpleTeam)


  /**
    * Check if the lobby can contains the given players.
    *
    * @param team
    * First team.
    * @param opponents
    * Opponents.
    * @return
    * The lobby status.
    * OK -> can contains the teams.
    * REVERSE -> can contains the teams but with reversed position.
    * FULL -> can't contains teams.
    */
  def canContains(team: SimpleTeam, opponents: Option[SimpleTeam]): LobbyStatusResponse

}

case class LobbyImpl(onFullLobby: Lobby => Unit,
                     team1: SimpleTeam = SimpleTeam("Team1"),
                     team2: SimpleTeam = SimpleTeam("Team2")) extends Lobby {

  val id = System.currentTimeMillis().toString
  println("New lobby on ID -> " + id)

  /**
    * Try to add a player to the lobby.
    *
    * @param player
    * Player ID
    * @throws it.unibo.pps2017.server.model.FullLobbyException
    * If the lobby is full.
    */
  override def addPlayer(player: String): Unit = {
    try {
      team1.addPlayer(player)
    } catch {
      case _: FullTeamException =>
        try {
          team2.addPlayer(player)
        } catch {
          case _: FullTeamException => throw FullLobbyException()
        }
    } finally {
      checkFullLobby()
    }
  }

  /**
    * Try to add a team to the lobby.
    *
    * @param team
    * Simple team with player's IDs
    * @throws it.unibo.pps2017.server.model.FullLobbyException
    * If the lobby can't contain a new team.
    */
  override def addTeam(team: SimpleTeam): Unit = {
    if (team1.canContains(team)) {
      team.getMembers.foreach(team1.addPlayer)
    } else if (team2.canContains(team)) {
      team.getMembers.foreach(team2.addPlayer)
    } else {
      throw FullLobbyException()
    }

    checkFullLobby()
  }

  private def checkFullLobby(): Unit = {
    if (team1.isFull && team2.isFull) {
      onFullLobby(this)
    }
  }

  /**
    * Return TRUE if there are 4 players in the lobby,
    * FALSE otherwise.
    *
    * @return
    * TRUE if there are 4 players in the lobby,
    * FALSE otherwise
    */
  override def isFull: Boolean = team1.isFull && team2.isFull

  /**
    * Check if the lobby can contains the given players.
    *
    * Opponents.
    * @return
    * The lobby status.
    * OK -> can contains the teams.
    * REVERSE -> can contains the teams but with reversed position.
    * FULL -> can't contains teams.
    */
  override def canContains(team: SimpleTeam, opponentsTeam: Option[SimpleTeam] = None): LobbyStatusResponse = {
    var status: LobbyStatusResponse = OK
    opponentsTeam match {
      case Some(opponents) =>
        if (team1.numberOfMembers + team.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT
          && team2.numberOfMembers + opponents.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT) {
          status = OK
        } else if (team2.numberOfMembers + team.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT
          && team1.numberOfMembers + opponents.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT) {
          status = REVERSE
        } else {
          status = FULL
        }


      case None =>
        if (team1.numberOfMembers + team.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT) {
          status = OK
        } else if (team2.numberOfMembers + team.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT) {
          status = OK
        } else {
          status = FULL
        }

    }


    status
  }
}

object LobbyStatusResponse {

  sealed trait LobbyStatusResponse {
    def asString: String
  }

  case object OK extends LobbyStatusResponse {
    override val asString: String = "Ok"
  }

  case object REVERSE extends LobbyStatusResponse {
    override val asString: String = "Reverse"
  }

  case object FULL extends LobbyStatusResponse {
    override val asString: String = "Full"
  }


  /**
    * This method is used to get all the available seeds
    *
    * @return a Iterable containing all the available seeds.
    */
  def values: Iterable[LobbyStatusResponse] = Iterable(OK, REVERSE, FULL)

}

object GameType {
  sealed trait  GameType {
    def asString: String
  }

  case object RANKED extends GameType {
    override val asString: String = "ranked"
  }

  case object UNRANKED extends GameType {
    override val asString: String = "unranked"
  }


  /**
    * This method is used to get all the available seeds
    *
    * @return a Iterable containing all the available seeds.
    */
  def values: Iterable[GameType] = Iterable(RANKED, UNRANKED)
}


final case class FullLobbyException(message: String = "The lobby don't have the seats required",
                                    private val cause: Throwable = None.orNull)
  extends Exception(message, cause)