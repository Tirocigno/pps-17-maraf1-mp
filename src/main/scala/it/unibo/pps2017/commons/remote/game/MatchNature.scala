
package it.unibo.pps2017.commons.remote.game

object MatchNature {

  /**
    * Define if a match is competitive or not.
    */
  sealed trait MatchNature

  /**
    * Competitive Match object.
    */
  case object CompetitiveMatch extends MatchNature

  /**
    * Non competitive match object.
    */
  case object CasualMatch extends MatchNature

}
