package it.unibo.pps2017.core.game

import it.unibo.pps2017.core.player.Controller


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

  val queue: Seq[Controller] = Seq[Controller](team1.firstMember.get,
    team2.firstMember.get, team1.secondMember.get, team2.secondMember.get)

  private var tokenIndex: Int = 0

  def next(): Controller = {
    val current: Int = tokenIndex
    tokenIndex = getNextIndex

    queue(current)
  }


  def setFirst(player: Controller): Unit = tokenIndex = queue.indexOf(player)

  def getCurrent: Controller = queue(tokenIndex)

  def getNext: Controller = queue(getNextIndex)

  private def getNextIndex: Int = (tokenIndex + 1) % MatchManager.MAX_PLAYER_NUMBER

}

final case class TeamNotReadyException(message: String = "Missing one or more element in the team",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)
