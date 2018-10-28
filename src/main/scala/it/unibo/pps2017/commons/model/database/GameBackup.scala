package it.unibo.pps2017.commons.model.database

import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed

/**
  * This class is used for build a game history.
  * At the end it will save all the game in the database.
  */
trait GameBackup {

  /**
    * The game's ID.
    *
    * @return
    * the game's ID.
    */
  def gameId: String

  /**
    * At game start.
    *
    * @param players
    * List of players in game.
    */
  def startGame(players: Seq[String])

  /**
    * At set start.
    *
    * @param cards
    * cards in the hand of each player .
    * @param briscola
    * Valid briscola for this set.
    */
  def startSet(cards: Map[String, Set[Card]], briscola: Seed)


  /**
    * At hand start.
    */
  def startHand()


  /**
    * Add a move to the current hand.
    *
    * @param player
    * Player who played the card.
    * @param card
    * card played.
    */
  def addMove(player: String, card: Card)

  /**
    * Save the hand's taker.
    *
    * @param taker
    * player who take the hand.
    */
  def endHand(taker: String)

  /**
    * At set end.
    * Save the score at this point.
    *
    * @param team1Score
    * team1's score.
    * @param team2Score
    * team2's score.
    */
  def endSet(team1Score: Int, team2Score: Int)


  /**
    * At game ending.
    * Save the winners.
    *
    * @param winners
    * game's winners.
    */
  def endGame(winners: Seq[String])
}
