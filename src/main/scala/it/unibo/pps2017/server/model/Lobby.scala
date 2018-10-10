
package it.unibo.pps2017.server.model

import it.unibo.pps2017.core.game.{FullTeamException, SimpleTeam, Team}
import it.unibo.pps2017.core.player.PlayerImpl

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

}

case class LobbyImpl(onFullLobby: Lobby => Unit,
                     id: String = System.currentTimeMillis().toString,
                     team1: SimpleTeam = SimpleTeam(),
                     team2: SimpleTeam = SimpleTeam()) extends Lobby {

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
    if (!team1.hasMember) {
      team.getMembers.foreach(team1.addPlayer(_))
    } else if (!team2.hasMember) {
      team.getMembers.foreach(team2.addPlayer(_))
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

}


final case class FullLobbyException(message: String = "The lobby don't have the seats required",
                                    private val cause: Throwable = None.orNull)
  extends Exception(message, cause)