
package it.unibo.pps2017.discovery

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
