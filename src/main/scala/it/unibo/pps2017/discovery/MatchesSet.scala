
package it.unibo.pps2017.discovery

import scala.collection.SortedSet

/**
  * This trait model a collection of current played matches in whole system.
  */
trait MatchesSet {

  /**
    * Add a match to the list.
    * @param matchRef the reference of the match to register.
    */
  def addMatch(matchRef: MatchRef):Unit

  /**
    * Remove a match from the list.
    * @param matchRef the match to remove.
    */
  def removeMatch(matchRef: MatchRef):Unit

  /**
    * Return all the current played matches.
    * @return
    */
  def getAllMatches:Set[MatchRef]
}

object MatchesSet {

  def apply: MatchesSet = new MatchesSetImpl()

  private class MatchesSetImpl extends MatchesSet {
    private var matchesSet = SortedSet[MatchRef]()

    override def addMatch(matchRef: MatchRef): Unit = matchesSet += matchRef

    override def removeMatch(matchRef: MatchRef): Unit = matchesSet -= matchRef

    override def getAllMatches: Set[MatchRef] = matchesSet.toSet

  }
}
