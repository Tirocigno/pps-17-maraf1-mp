package it.unibo.pps2017.core.game

import java.util
import java.util.stream

import it.unibo.pps2017.core.deck.{ComposedDeck, GameDeck}
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.core.game.MatchManager._
import it.unibo.pps2017.core.player.Controller

import scala.collection.mutable.ListBuffer

class MatchManager(team1: Team = Team("Team1"),
                   team2: Team = Team("Team2")) extends Match {

  var currentBriscola: Seed = _
  var currentSuit: Seed = _
  var gameCycle: GameCycle = _
  var deck: GameDeck = ComposedDeck()


  if (team1.isFull && team2.isFull) onFullTable()

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

  private def onFullTable(): Unit = {
    gameCycle = GameCycle(team1, team2)
    startGame()
  }

  /**
    * Starting the game.
    */
  override def startGame(): Unit = {
    playSet()
  }

  /**
    * Check if all operations concerning the previous set are closed.
    * If it's all right, it shuffle the deck and start a new set.
    */
  override def playSet(): Unit = {
    deck.shuffle()
    var i: Int = 0
    deck.distribute().forEach(hand => {
      getPlayers(i).setHand(new util.HashSet(hand))
      i += 1
    })
  }

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
    if (card.cardSeed == currentSuit) return true

    val playerHand: stream.Stream[Card] = gameCycle.getCurrent.getHand.stream()

    if (playerHand.filter(_.cardSeed == currentSuit).count() == 0) return true

    false
  }

  /**
    * Return the first team.
    *
    * @return
    * the first team.
    */
  def firstTeam(): Team = team1

  /**
    * Return the second team.
    *
    * @return
    * the second team.
    */
  def secondTeam(): Team = team2

  /**
    * Return the players in the match.
    * If the game is started return the game queue, otherwise, before all the members
    * of the first team and then the players of the second
    *
    * @return
    * players in the match.
    */
  def getPlayers: Seq[Controller] = {
    if (gameCycle != null) return gameCycle.queue

    team1.getMembers ++ team2.getMembers
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


  /**
    * Return the first player of the team.
    *
    * @return
    * the first player of the team.
    */
  def firstMember: Option[Controller] = members.headOption

  /**
    * Return the second player of the team.
    *
    * @return
    * the second player of the team.
    */
  def secondMember: Option[Controller] = members.lastOption

  /**
    * Return the actual number of players in the team.
    *
    * @return
    * the number of players in the team.
    */
  def numberOfMembers: Int = members.length

  /**
    * Return the members of the team.
    *
    * @return
    * the members of the team
    */
  def getMembers: Seq[Controller] = members

  /**
    * Return TRUE if the team has reach two members, FALSE otherwise.
    *
    * @return
    * TRUE if the team has reach two members, FALSE otherwise.
    */
  def isFull: Boolean = numberOfMembers == MatchManager.TEAM_MEMBERS_LIMIT
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
  val MAX_HAND_CARDS: Int = 10

  def apply(team1: Team, team2: Team) = new MatchManager(team1, team2)

  def apply() = new MatchManager()
}




