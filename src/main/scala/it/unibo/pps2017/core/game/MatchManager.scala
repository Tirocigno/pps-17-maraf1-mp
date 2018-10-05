
package it.unibo.pps2017.core.game

import it.unibo.pps2017.core.deck.cards.Seed.{Coin, Seed}
import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}
import it.unibo.pps2017.core.deck.{ComposedDeck, GameDeck}
import it.unibo.pps2017.core.game.MatchManager._
import it.unibo.pps2017.core.player.Player

import scala.collection.mutable
import scala.util.Random


object MatchManager {
  val RANDOM_TEAM: String = "RANDOM_TEAM"
  val TEAM_MEMBERS_LIMIT: Int = 2
  val MAX_PLAYER_NUMBER: Int = 4
  val MAX_HAND_CARDS: Int = 10
  val FOUR_OF_COIN: Card = CardImpl(Coin, 4)
  val MAX_SCORE: Int = 41
  val ACE_VALUE: Int = 1
  val TWO_VALUE: Int = 2
  val THREE_VALUE: Int = 3
  val REQUIRED_NUMBERS_OF_CARDS_FOR_MARAFFA: Int = 3
  val EXTRA_POINTS_FOR_MARAFFA: Int = 3

  def apply(team1: Team, team2: Team) = new MatchManager(team1, team2)

  def apply() = new MatchManager()
}


class MatchManager(team1: Team = Team(firstTeamID),
                   team2: Team = Team(secondTeamId)) extends Match {

  var currentBriscola: Option[Seed] = None
  var currentSuit: Option[Seed] = None
  var gameCycle: GameCycle = _
  var deck: GameDeck = ComposedDeck()
  var firstHand: Boolean = true
  val cardsOnTable: mutable.ListBuffer[(Card, Player)] = mutable.ListBuffer()
  var nextHandStarter: Option[Player] = None
  var hasMaraffa: Option[Team] = None
  var setEnd: Boolean = false
  var gameEnd: Boolean = false


  if (team1.isFull && team2.isFull) {
    onFullTable()
  }


  /**
    * Found the taker in a List of cards.
    *
    * @param hand
    * List of played cards with the player who have threw the card.
    * @return
    * The hand taker.
    */
  implicit def defineTaker(hand: mutable.ListBuffer[(Card, Player)]): Player = {
    var max: Card = hand.head._1

    hand foreach (tuple => {
      val card = tuple._1
      if (card.cardSeed == currentBriscola.get) {
        if (max.cardSeed != currentBriscola.get) {
          max = card
        } else if (max < card) {
          max = card
        }
      } else if (max.cardSeed != currentBriscola.get && card.cardSeed == currentSuit.get && max < card) {
        max = card
      }
    })

    hand.filter(_._1 == max).head._2
  }


  /**
    * Add a player to the match.
    *
    * @param newPlayer
    * new player to add.
    * @param team
    * Team name to add the player. Not specify for random imputation.
    */
  override def addPlayer(newPlayer: Player, team: String = RANDOM_TEAM): Unit = {
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
  private def addPlayerToTeam(player: Player, teamName: String = RANDOM_TEAM): Unit = {
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

  /**
    * Notify the full table status.
    * Create the cycle and start the game.
    */
  private def onFullTable(): Unit = {
    gameCycle = GameCycle(team1, team2)
    startGame()
  }

  /**
    * Starting the game.
    */
  override def startGame(): Unit = {
    firstHand = true
    startSet()
  }

  /**
    * Reset variables and start the set.
    */
  private def startSet(): Unit = {
    playSet()
  }

  /**
    * Check if all operations concerning the previous set are closed.
    * If it's all right, it shuffle the deck and start a new set.
    */
  override def playSet(): Unit = {
    prepareSet()

    nextHandStarter match {
      case Some(player) => setBriscola(player.onSetBriscola())
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
    }
  }

  /**
    * Prepare the table, shuffle the deck and distribute cards to all players.
    */
  private def prepareSet(): Unit = {
    setEnd = false
    deck.shuffle()
    var i: Int = 0
    deck.distribute().foreach(hand => {
      getPlayers(i).setHand(hand)
      if (firstHand) {
        if (isFirstPlayer(hand)) {
          nextHandStarter = Some(getPlayers(i))
          firstHand = false
        }
      }
      i += 1
    })
  }

  /**
    * On the event card played.
    *
    * @param card
    * played card.
    */
  private def onCardPlayed(card: Card): Unit = {
    if (gameCycle.isFirst) onFirstCardOfHand(card)

    cardsOnTable += ((card, gameCycle.getCurrent))

    if (!gameCycle.isLast) {
      gameCycle.next().onMyTurn()
    } else {
      onHandEnd(cardsOnTable)
    }
  }


  /**
    * Start the hand.
    * Set the first player and it notify him.
    */
  private def startHand(): Unit = {
    nextHandStarter match {
      case Some(player) => gameCycle.setFirst(player)
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
    }
    gameCycle.getCurrent.onMyTurn()
  }


  /**
    * On the hand finish.
    * Add the cards to the last taker team
    * and reset some variables.
    *
    * @param lastTaker
    * The hand taker.
    */
  private def onHandEnd(lastTaker: Player): Unit = {
    deck.registerTurnPlayedCards(cardsOnTable.map(_._1), getTeamIndexOfPlayer(lastTaker))

    nextHandStarter = Some(lastTaker)
    currentSuit = None
    cardsOnTable.clear()


    nextHandStarter match {
      case Some(player) => if (player.getHand().isEmpty) onSetEnd()
      case None => throw new Exception("FirstPlayerOfTheHand Not Found")
    }
  }

  /**
    * On first card playing event.
    *
    * @param card
    * first card played.
    */
  private def onFirstCardOfHand(card: Card): Unit = {
    val currentHand: Set[Card] = gameCycle.getCurrent.getHand()
    if (currentHand.size == MAX_HAND_CARDS && currentBriscola.get == card.cardSeed && card.cardValue == ACE_VALUE) {
      hasMaraffa = checkMaraffa(currentHand, gameCycle.getCurrent)
    }
    currentSuit = Option(card.cardSeed)
  }

  /**
    * Check if the player have the Marafona in his hand.
    *
    * @param hand
    * player's hand.
    * @param player
    * current player.
    * @return
    * Team of the player if he has the Marafona, None otherwise.
    */
  private def checkMaraffa(hand: Set[Card], player: Player): Option[Team] = {
    if (hand.filter(searchAce => searchAce.cardSeed == currentSuit.get)
      .count(c => c.cardValue == ACE_VALUE || c.cardValue == TWO_VALUE || c.cardValue == THREE_VALUE) == REQUIRED_NUMBERS_OF_CARDS_FOR_MARAFFA) {
      Some(getTeamOfPlayer(player))
    }

    None
  }

  /**
    * Set end state.
    * Reset variables and check for a winner.
    * If there isn't a winner start a new set.
    */
  private def onSetEnd(): Unit = {
    setEnd = true
    currentBriscola = None
    nextHandStarter = None

    val setScore = deck.computeSetScore()

    hasMaraffa match {
      case Some(team) =>
        if (team1 == team) {
          team1.addPoints(setScore._1.toInt + EXTRA_POINTS_FOR_MARAFFA)
          team2.addPoints(setScore._2.toInt)
        } else {
          team1.addPoints(setScore._1.toInt)
          team2.addPoints(setScore._2.toInt + EXTRA_POINTS_FOR_MARAFFA)
        }
      case None =>
        team1.addPoints(setScore._1)
        team2.addPoints(setScore._2)
    }

    hasMaraffa = None

    getGameWinner match {
      case Some(team) => notifyWinner(team)
      case None => startSet()
    }
  }

  /**
    * Setting the briscola for the current set.
    *
    * @param seed
    * Current briscola's seed.
    */
  override def setBriscola(seed: Seed.Seed): Unit = {
    currentBriscola = Option(seed)

    startHand()
  }

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
  override def isCardOk(card: Card): Boolean = currentSuit match {
    case Some(seed) =>
      if (seed == card.cardSeed) {
        onCardPlayed(card)
        return true
      }

      val playerHand: Seq[Card] = gameCycle.getCurrent.getHand().toStream

      if (!playerHand.exists(_.cardSeed == seed)) {
        onCardPlayed(card)
        return true
      }

      false
    case None =>
      onCardPlayed(card)
      true
  }

  /**
    * Play a random card in the hand of the player.
    *
    * @param player
    * Reference player.
    * @return
    * A random card among those that the player can drop.
    */
  override def forcePlay(player: Player): Card = {
    currentSuit match {
      case Some(seed) =>
        val rightCards: Seq[Card] = getPlayers(getPlayers.indexOf(player)).getHand().filter(_.cardSeed == seed).toList

        rightCards(Random.nextInt(rightCards.size))
      case None =>
        val playerHand: Seq[Card] = getPlayers(getPlayers.indexOf(player)).getHand().toList

        playerHand(Random.nextInt(playerHand.size))
    }
  }


  /**
    * If set is end return the score of teams, and a true if the game is end, false otherwise.
    *
    * @return
    * If set is end return the score of teams, and a true if the game is end, false otherwise.
    */
  override def isSetEnd: Option[(Int, Int, Boolean)] = {
    if (setEnd) {
      Some((team1.getScore, team2.getScore, gameEnd))
    }

    None
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
  def getPlayers: Seq[Player] = {
    if (gameCycle != null) return gameCycle.queue

    team1.getMembers ++ team2.getMembers
  }


  /**
    * Search the Four of coin in the hand.
    * If found it return TRUE, otherwise FALSE.
    *
    * @param hand
    * Full player hand.
    * @return
    * TRUE if found in the hand the four of coin, FALSE otherwise.
    */
  private def isFirstPlayer(hand: Set[Card]): Boolean = hand.contains(FOUR_OF_COIN)


  /**
    * Return the team's index of the player.
    * 0 if is in the first team, 1 otherwise.
    *
    * @param player
    * researched player.
    * @return
    * team index.
    */
  private def getTeamIndexOfPlayer(player: Player): Int = {
    if (team1.getMembers.contains(player)) return 0

    1
  }

  /**
    * Return the player's team.
    *
    * @param player
    * researched player.
    * @return
    * The team.
    */
  private def getTeamOfPlayer(player: Player): Team = getTeamIndexOfPlayer(player) match {
    case 0 => team1
    case 1 => team2
  }

  /**
    * Check if one of the teams has win the game.
    *
    * @return
    * return the game winner team.
    */
  private def getGameWinner: Option[Team] = {
    if (team1.getScore >= MAX_SCORE && team1.getScore > team2.getScore) return Some(team1)

    if (team2.getScore >= MAX_SCORE && team2.getScore > team1.getScore) return Some(team2)

    None
  }


  /**
    * Notify winner.
    *
    * @param team
    * Team winning.
    */
  //TODO
  def notifyWinner(team: Team): Unit = gameEnd = true


}


final case class FullTeamException(message: String = "The team has reached the maximum number of players",
                                   private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

final case class TeamNotFoundException(message: String = "Team not found in this match",
                                       private val cause: Throwable = None.orNull)
  extends Exception(message, cause)




