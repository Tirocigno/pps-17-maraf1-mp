package it.unibo.pps2017.commons.model.database
import it.unibo.pps2017.core.deck.cards.{Card, Seed}

class GameBackupImpl(override val gameId: String) extends GameBackup {


  /**
    * At game start.
    *
    * @param players
    * List of players in game.
    */
  override def startGame(players: Seq[String]): Unit = ???

  /**
    * At set start.
    *
    * @param cards
    * cards in the hand of each player .
    * @param briscola
    * Valid briscola for this set.
    */
  override def startSet(cards: Map[String, Set[Card]], briscola: Seed.Seed): Unit = ???

  /**
    * At hand start.
    */
  override def startHand(): Unit = ???

  /**
    * Add a move to the current hand.
    *
    * @param player
    * Player who played the card.
    * @param card
    * card played.
    */
  override def addMove(player: String, card: Card): Unit = ???

  /**
    * Save the hand's taker.
    *
    * @param taker
    * player who take the hand.
    */
  override def endHand(taker: String): Unit = ???

  /**
    * At set end.
    * Save the score at this point.
    *
    * @param team1Score
    * team1's score.
    * @param team2Score
    * team2's score.
    */
  override def endSet(team1Score: Int, team2Score: Int): Unit = ???

  /**
    * At game ending.
    * Save the winners.
    *
    * @param winners
    * game's winners.
    */
  override def endGame(winners: Seq[String]): Unit = ???
}
