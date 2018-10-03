package it.unibo.pps2017.core.game

import it.unibo.pps2017.core.player.Player




/**
  * This class manage the match turning.
  *
  * @param team1
  * First team.
  * @param team2
  * The other team.
  */
case class GameCycle(team1: Team,
                     team2: Team) {


  if (team1.numberOfMembers != MatchManager.TEAM_MEMBERS_LIMIT || team2.numberOfMembers != MatchManager.TEAM_MEMBERS_LIMIT) {
    throw TeamNotReadyException()
  }

  val queue: Seq[Player] = Seq[Player](team1.firstMember.get,
    team2.firstMember.get, team1.secondMember.get, team2.secondMember.get)

  private var tokenIndex: Int = 0
  private var firstPlayer: Option[Player] = None

  /**
    * Return a player and update the index to the next.
    *
    * @return
    * The player who must play his card.
    */
  def next(): Player = {
    tokenIndex = getNextIndex

    queue(tokenIndex)
  }


  /**
    * Set the first player in the current hand.
    *
    * @param player
    * The player who have to open the hand.
    */
  def setFirst(player: Player): Unit = {
    tokenIndex = queue.indexOf(player)
    firstPlayer = Option(player)
  }

  /**
    * Return TRUE if the player is the first of the current turn, FALSE otherwise.
    *
    * @return
    * TRUE if the player is the first of the current turn, FALSE otherwise.
    */
  def isFirst: Boolean = {
    firstPlayer match {
      case Some(first) =>
        first == getCurrent
      case None =>
        false
    }
  }

  /**
    * Return TRUE if the player is the last of the current turn, FALSE otherwise.
    *
    * @return
    * TRUE if the player is the last of the current turn, FALSE otherwise.
    */
  def isLast: Boolean = {
    firstPlayer match {
      case Some(first) =>
        getCurrent == getPrevOf(first)
      case None =>
        false
    }
  }

  /**
    * Return the player who have to play his card.
    *
    * @return
    * The player who have to play his card.
    */
  def getCurrent: Player = queue(tokenIndex)

  /**
    * Return the next player in the queue.
    *
    * @return
    * the next player in the queue.
    */
  def getNext: Player = queue(getNextIndex)

  /**
    * Return a sequence of all the players in the game.
    *
    * @return
    * a sequence of all the players in the game.
    */
  def getPlayers: Seq[Player] = queue

  /**
    * Calculate the next index for the queue. If it's end, the index will reset.
    *
    * @return
    * The next player index in the queue.
    */
  private def getNextIndex: Int = (tokenIndex + 1) % MatchManager.MAX_PLAYER_NUMBER

  /**
    * Calculate the previous element of the queue.
    *
    * @param player
    * Reference element.
    * @return
    * The previous player.
    */
  private def getPrevOf(player: Player): Player = {
    val prevIndex: Int = (MatchManager.MAX_PLAYER_NUMBER + (getPlayers.indexOf(player) - 1)) % MatchManager.MAX_PLAYER_NUMBER

    getPlayers(prevIndex)
  }

}

final case class TeamNotReadyException(message: String = "Missing one or more element in the team",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)
