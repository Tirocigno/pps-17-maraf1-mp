package it.unibo.pps2017.core.game

import java.util.stream

import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.core.game.MatchManager._
import it.unibo.pps2017.core.player.Controller

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class MatchManager(players: mutable.ListBuffer[Controller] = mutable.ListBuffer(),
                        team1: Team = Team("Team1"),
                        team2: Team = Team("Team2")) extends Match {

  var currentBriscola: Seed = _
  var currentSuit: Seed = _
  var gameCycle: GameCycle = _


  /**
    * Add a player to the match.
    *
    * @param newPlayer
    * new player to add.
    * @param team
    * Team name to add the player. Not specify for random imputation.
    */
  override def addPlayer(newPlayer: Controller, team: String = RANDOM_TEAM): Unit = {
    try {
      addPlayerToTeam(newPlayer, team)
      players += newPlayer
    } catch {
      case teamNotFoundException: TeamNotFoundException => println("EXCEPTION /" + teamNotFoundException.message)
      case fullTeamException: FullTeamException => println("EXCEPTION /" + fullTeamException.message)
    }
  }


  /**
    * Add a player to one team.
    *
    * @param player
    * Player that had to join to one of the team.
    * @param teamName
    * Team name, if it isn't specified the player is added in the first free space.
    * @throws it.unibo.pps2017.core.game.FullTeamException
    * If all the teams has two members.
    * @throws it.unibo.pps2017.core.game.TeamNotFoundException
    * If the specified team isn't in this match.
    */
  @throws(classOf[FullTeamException])
  @throws(classOf[TeamNotFoundException])
  private def addPlayerToTeam(player: Controller, teamName: String = RANDOM_TEAM): Unit = {
    if (teamName.equals(RANDOM_TEAM)) {
      try {
        team1.addPlayer(player)
      } catch {
        case _: FullTeamException =>
          try {
            team2.addPlayer(player)
          } catch {
            case _: FullTeamException => throw FullTeamException()
          }
      }
    } else {
      getTeamForName(teamName).addPlayer(player)
    }
  }


  /**
    * Get the team at the specified name.
    *
    * @param teamName
    * Team name to get returned.
    * @throws it.unibo.pps2017.core.game.TeamNotFoundException
    * If the specified team isn't in this match.
    * @return
    * The respective team.
    */
  @throws(classOf[TeamNotFoundException])
  private def getTeamForName(teamName: String): Team = {
    if (team1.name.equalsIgnoreCase(teamName)) return team1

    if (team2.name.equalsIgnoreCase(teamName)) return team2

    throw TeamNotFoundException("Team '" + teamName + "' not found in this match")
  }

  private def onFullTable(): Unit = {}

  /**
    * Starting the game.
    */
  override def startGame(): Unit = ???

  /**
    * Check if all operations concerning the previous set are closed.
    * If it's all right, it shuffle the deck and start a new set.
    */
  override def playSet(): Unit = ???

  /**
    * Setting the briscola for the current set.
    *
    * @param seed
    * Current briscola's seed.
    */
  override def setBriscola(seed: Seed.Seed): Unit = currentBriscola = seed

  /**
    * Check if the played card is accepted.
    * The card may be refused if it is not the current suit but the player has one in his hand
    *
    * @param card
    * Played card.
    * @return
    * True if the card's suit is correct.
    * False otherwise.
    */
  override def isCardOk(card: Card): Boolean = {

    if (card.cardSeed == currentSuit) {
      return true
    }

    val playerHand: stream.Stream[Card] = gameCycle.getCurrent.getHand.stream()
    playerHand.filter(_.cardSeed == currentSuit)

    if (playerHand.count() == 0) {
      return true
    }

    false
  }
}


/**
  * This class manage a Team. Identified by a name and a list of members.
  *
  * @param name
  * Name of the team
  * @param members
  * Members of the team. Limited at max 2.
  */
case class Team(var name: String,
                private var members: ListBuffer[Controller] = ListBuffer()) {

  /**
    * Add a player to the team.
    *
    * @param newPlayer
    * The player who join the team.
    * @throws it.unibo.pps2017.core.game.FullTeamException
    * If the team has already 2 members.
    */
  @throws(classOf[FullTeamException])
  def addPlayer(newPlayer: Controller): Unit = {
    if (members.length >= TEAM_MEMBERS_LIMIT) {
      throw FullTeamException()
    }

    members += newPlayer
  }


  def firstMember: Option[Controller] = members.headOption

  def secondMember: Option[Controller] = members.lastOption

  def numberOfMembers: Int = members.length
}

final case class FullTeamException(message: String = "The team has reached the maximum number of players",
                                   private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

final case class TeamNotFoundException(message: String = "Team not found in this match",
                                       private val cause: Throwable = None.orNull)
  extends Exception(message, cause)


object MatchManager {
  val RANDOM_TEAM: String = "RANDOM_TEAM"
  val TEAM_MEMBERS_LIMIT: Int = 2
  val MAX_PLAYER_NUMBER: Int = 4
}




