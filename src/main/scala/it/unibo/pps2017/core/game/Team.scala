package it.unibo.pps2017.core.game

import it.unibo.pps2017.core.game.MatchManager._
import it.unibo.pps2017.core.player.{ PlayerActor}

import scala.collection.mutable.ListBuffer

/**
  * This class manage a Team. Identified by a name and a list of members.
  *
  * @param name
  * Name of the team
  * @param members
  * Members of the team. Limited at max 2.
  */
case class Team(var name: String,
                private var members: ListBuffer[PlayerActor] = ListBuffer(),
                private var score: Int = 0) {

  /**
    * Add a player to the team.
    *
    * @param newPlayer
    * The player who join the team.
    * @throws it.unibo.pps2017.core.game.FullTeamException
    * If the team has already 2 members.
    */
  @throws(classOf[FullTeamException])
  def addPlayer(newPlayer: PlayerActor): Unit = {
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
  def firstMember: Option[PlayerActor] = members.headOption

  /**
    * Return the second player of the team.
    *
    * @return
    * the second player of the team.
    */
  def secondMember: Option[PlayerActor] = members.lastOption

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
  def getMembers: Seq[PlayerActor] = members

  /**
    * Add set's point to the team score.
    *
    * @param score
    * Set's points.
    */
  def addPoints(score: Int): Unit = this.score += score

  /**
    * Return the current team's score.
    *
    * @return
    * the current team's score.
    */
  def getScore: Int = score


  /**
    * Return TRUE if the team has reach two members, FALSE otherwise.
    *
    * @return
    * TRUE if the team has reach two members, FALSE otherwise.
    */
  def isFull: Boolean = numberOfMembers == TEAM_MEMBERS_LIMIT
}