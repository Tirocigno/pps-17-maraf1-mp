
package it.unibo.pps2017.server.actor

import akka.actor.Actor
import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.server.model._

import scala.collection.mutable.ListBuffer


//noinspection ScalaStyle
class LobbyActor extends Actor {
  val allLobby: ListBuffer[Lobby] = ListBuffer()

  override def receive: Receive = {
    /**
      * Search a game for a single player.
      */
    case SinglePlayerMsg(player, gameFoundEvent) =>
      self ! SearchPlayerMsg(allLobby.headOption, player, gameFoundEvent)

    /**
      * Search a game for a team.
      */
    case MultiPlayerMsg(player, mate, gameFoundEvent) =>
      val team: SimpleTeam = SimpleTeam(ListBuffer(player, mate))

      self ! SearchTeamMsg(allLobby.headOption, team, gameFoundEvent)

    /**
      * Internal message for searching a game for one player.
      */
    case SearchPlayerMsg(currentLobby, player, gameFoundEvent) =>
      currentLobby match {
        case Some(lobby) =>
          try {
            lobby.addPlayer(player)
            notifyGameFound(lobby, gameFoundEvent)
          } catch {
            case _: FullLobbyException =>
              try {
                self ! SearchPlayerMsg(Some(allLobby(allLobby.indexOf(lobby) + 1)), player, gameFoundEvent)
              } catch {
                case _: Throwable => self ! SearchPlayerMsg(None, player, gameFoundEvent)
              }
            case ex: Exception => ex.printStackTrace()
          }

        case None =>
          createLobbyAndNotify(player, gameFoundEvent)
      }

    /**
      * Internal message for searching a game for team.
      */
    case SearchTeamMsg(currentLobby, team, gameFoundEvent) =>
      currentLobby match {
        case Some(lobby) =>
          try {
            lobby.addTeam(team)
            notifyGameFound(lobby, gameFoundEvent)
          } catch {
            case _: FullLobbyException =>
              try {
                self ! SearchTeamMsg(Some(allLobby(allLobby.indexOf(lobby) + 1)), team, gameFoundEvent)
              } catch {
                case _: Throwable => self ! SearchTeamMsg(None, team, gameFoundEvent)
              }
            case ex: Exception => ex.printStackTrace()
          }

        case None =>
          createLobbyAndNotify(team, gameFoundEvent)
      }


  }


  /**
    * Notify when the lobby is found.
    * Remove the lobby if is full.
    *
    * @param game
    * Lobby.
    * @param onGameFound
    * Event to fire.
    */
  private def notifyGameFound(game: Lobby, onGameFound: String => Unit): Unit = {
    if (game.isFull) {
      allLobby -= game
      //TODO Deploy to match
    }
    onGameFound(game.id)
  }

  /**
    * Create the lobby for the player and fire the event param.
    *
    * @param player
    * Player id.
    * @param onGameFound
    * Event.
    */
  private def createLobbyAndNotify(player: String, onGameFound: String => Unit): Unit = {
    val lobby = createLobbyAndNotify(onGameFound)

    lobby.addPlayer(player)
    allLobby += lobby
  }


  /**
    * Create the lobby for the team and fire the event param.
    *
    * @param team
    * Team.
    * @param onGameFound
    * Event.
    */
  private def createLobbyAndNotify(team: SimpleTeam, onGameFound: String => Unit): Unit = {
    val lobby = createLobbyAndNotify(onGameFound)

    lobby.addTeam(team)
    allLobby += lobby
  }

  private def createLobbyAndNotify(onGameFound: String => Unit): Lobby = {
    val newLobby: Lobby = LobbyImpl(allLobby -= _)
    onGameFound(newLobby.id)

    newLobby
  }
}
