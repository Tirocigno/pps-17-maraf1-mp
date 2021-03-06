
package it.unibo.pps2017.core.game

import it.unibo.pps2017.server.actor.GameActor._
import it.unibo.pps2017.server.actor.{FullTeamException, GameActor}
import it.unibo.pps2017.server.model.Side

import scala.collection.mutable.ListBuffer


sealed trait BaseTeam[A] {

  var teamIndex: String

  /**
    * Add a player to the team.
    *
    * @param newPlayer
    * The player who join the team.
    * @throws FullTeamException
    * If the team has already 2 members.
    */
  @throws(classOf[FullTeamException])
  def addPlayer(newPlayer: A): Unit


  /**
    * Return the first player of the team.
    *
    * @return
    * the first player of the team.
    */
  def firstMember: Option[A]

  /**
    * Return the second player of the team.
    *
    * @return
    * the second player of the team.
    */
  def secondMember: Option[A]

  /**
    * Return the actual number of players in the team.
    *
    * @return
    * the number of players in the team.
    */
  def numberOfMembers: Int

  /**
    * Return the members of the team.
    *
    * @return
    * the members of the team
    */
  def getMembers: Seq[A]

  /**
    * Set set's point to the team score.
    *
    * @param score
    * Set's points.
    */
  def setPoints(score: Int): Unit

  /**
    * Add set's point to the team score.
    *
    * @param score
    * Set's points.
    */
  def addPoints(score: Int): Unit

  /**
    * Return the current team's score.
    *
    * @return
    * the current team's score.
    */
  def getScore: Int


  /**
    * Return TRUE if the team has reach two members, FALSE otherwise.
    *
    * @return
    * TRUE if the team has reach two members, FALSE otherwise.
    */
  def isFull: Boolean

  /**
    * Return TRUE if the team has almost one member, FALSE otherwise.
    *
    * @return
    * TRUE if the team has almost one member, FALSE otherwise.
    */
  def hasMember: Boolean

  /**
    * Return the team composition with both player's username.
    *
    * @return
    * the team composition with both player's username.
    */
  def asSide: Side
}


/**
  *
  * @param teamIndex
  * @param members
  * @param score
  */
case class Team(override var teamIndex: String, private var members: ListBuffer[String] = ListBuffer(),
                private var score: Int = 0) extends BaseTeam[String] {

  /**
    * Add a player to the team.
    *
    * @param newPlayer
    * The player who join the team.
    * @throws FullTeamException
    * If the team has already 2 members.
    */
  override def addPlayer(newPlayer: String): Unit = {
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
  override def firstMember: Option[String] = members.headOption

  /**
    * Return the second player of the team.
    *
    * @return
    * the second player of the team.
    */
  override def secondMember: Option[String] = members.lastOption

  /**
    * Return the actual number of players in the team.
    *
    * @return
    * the number of players in the team.
    */
  override def numberOfMembers: Int = members.size

  /**
    * Return the members of the team.
    *
    * @return
    * the members of the team
    */
  override def getMembers: Seq[String] = members

  /**
    * Add set's point to the team score.
    *
    * @param score
    * Set's points.
    */
  override def addPoints(score: Int): Unit = this.score += score

  /**
    * Return the current team's score.
    *
    * @return
    * the current team's score.
    */
  override def getScore: Int = score

  /**
    * Return TRUE if the team has reach two members, FALSE otherwise.
    *
    * @return
    * TRUE if the team has reach two members, FALSE otherwise.
    */
  override def isFull: Boolean = numberOfMembers == TEAM_MEMBERS_LIMIT

  /**
    * Return TRUE if the team has almost one member, FALSE otherwise.
    *
    * @return
    * TRUE if the team has almost one member, FALSE otherwise.
    */
  override def hasMember: Boolean = members.nonEmpty

  /**
    * Return the team composition with both player's username.
    *
    * @return
    * the team composition with both player's username.
    */
  override def asSide: Side = {
    val app: ListBuffer[String] = ListBuffer()
    firstMember match {
      case Some(member) =>
        app += member
      case None =>
    }

    secondMember match {
      case Some(member) =>
        app += member
      case None =>
    }

    Side(app)
  }


  /**
    * Check if can contains the members in the given team.
    *
    * @param team
    * Team.
    * @return
    * TRUE if can contains the team, FALSE otherwise.
    */
  def canContains(team: Team): Boolean = {
    numberOfMembers + team.numberOfMembers <= GameActor.TEAM_MEMBERS_LIMIT
  }

  /**
    * Add set's point to the team score.
    *
    * @param score
    * Set's points.
    */
  override def setPoints(score: Int): Unit = {
    this.score = score
  }
}
