package it.unibo.pps2017.core.game

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl}
import it.unibo.pps2017.core.deck.cards.Seed.{Cup, Seed}
import it.unibo.pps2017.core.player.Controller

import scala.collection.mutable


/**
  * This class manage the match turning.
  *
  * @param team1
  * First team.
  * @param team2
  * The other team.
  */
case class GameCycle(team1: Team,
                     team2: Team,
                     onHandStart: Card => Unit) {


  if (team1.numberOfMembers != MatchManager.TEAM_MEMBERS_LIMIT || team2.numberOfMembers != MatchManager.TEAM_MEMBERS_LIMIT) {
    throw TeamNotReadyException()
  }

  val queue: Seq[Controller] = Seq[Controller](team1.firstMember.get,
    team2.firstMember.get, team1.secondMember.get, team2.secondMember.get)

  private var tokenIndex: Int = 0

  /**
    * Return a player and update the index to the next.
    *
    * @return
    * The player who must play his card.
    */
  def next(): Controller = {
    val current: Int = tokenIndex
    tokenIndex = getNextIndex

    queue(current)
  }

  /**
    * Return a classic hand turning, from the first player to the last.
    *
    * @param first
    * the first player to start the hand.
    * @return
    * A sequence with all the player. From the first to the last.
    */
  //TODO Not full implemented
  def handTurning(first: Controller): Map[Card, Controller] = {
    setFirst(first)
    val playedCards: mutable.Map[Card, Controller] = mutable.HashMap()

    (1 to 4).toStream foreach (turn => {
      val currentPlayer = next()
      //TODO
      //activePlayer.onMyTurn()
      val playedCard = CardImpl(Cup, 1)
      if (turn == 1) onHandStart(playedCard)

      playedCards += (playedCard -> currentPlayer)
    })

    playedCards.toMap
  }


  /**
    * Set the first player in the current hand.
    *
    * @param player
    * The player who have to open the hand.
    */
  def setFirst(player: Controller): Unit = tokenIndex = queue.indexOf(player)

  /**
    * Return the player who have to play his card.
    *
    * @return
    * The player who have to play his card.
    */
  def getCurrent: Controller = queue(tokenIndex)

  /**
    * Return the next player in the queue.
    *
    * @return
    * the next player in the queue.
    */
  def getNext: Controller = queue(getNextIndex)

  /**
    * Return a sequence of all the players in the game.
    *
    * @return
    * a sequence of all the players in the game.
    */
  def getPlayers: Seq[Controller] = queue

  /**
    * Calculate the next index for the queue. If it's end, the index will reset.
    *
    * @return
    * The next player index in the queue.
    */
  private def getNextIndex: Int = (tokenIndex + 1) % MatchManager.MAX_PLAYER_NUMBER

}

final case class TeamNotReadyException(message: String = "Missing one or more element in the team",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)
