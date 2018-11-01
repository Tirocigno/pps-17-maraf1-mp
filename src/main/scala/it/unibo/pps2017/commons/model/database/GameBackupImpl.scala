package it.unibo.pps2017.commons.model.database

import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.server.model.database.RedisGameUtils
import it.unibo.pps2017.server.model.{Game, GameSet, Hand, Move}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class GameBackupImpl(override val gameId: String) extends GameBackup {

  val players: mutable.ListBuffer[String] = ListBuffer()
  val sets: mutable.ListBuffer[GameSet] = ListBuffer()

  var currentSet: GameSet = _
  var currentSetHands: mutable.ListBuffer[Hand] = _
  var currentHand: mutable.ListBuffer[Move] = _


  /**
    * At game start.
    *
    * @param players
    * List of players in game.
    */
  override def startGame(players: Seq[String]): Unit = {
    players.map(this.players += _)
  }

  /**
    * At set start.
    *
    * @param cards
    * cards in the hand of each player .
    * @param briscola
    * Valid briscola for this set.
    */
  override def startSet(cards: Map[String, Set[Card]], briscola: Seed.Seed): Unit = {
    currentSet = GameSet(cards.map({ case (k, v) => (k, v.map(card => card.cardValue + card.cardSeed.asString)) }), Seq(), briscola.asString, 0, 0)
    currentSetHands = ListBuffer()
  }

  /**
    * At hand start.
    */
  override def startHand(): Unit = {
    currentHand = ListBuffer()
  }

  /**
    * Add a move to the current hand.
    *
    * @param player
    * Player who played the card.
    * @param card
    * card played.
    */
  override def addMove(player: String, card: Card): Unit = {
    currentHand += Move(player, card.cardValue + card.cardSeed.asString )
  }

  /**
    * Save the hand's taker.
    *
    * @param taker
    * player who take the hand.
    */
  override def endHand(taker: String): Unit = {
    currentSetHands += Hand(currentHand, taker)
  }

  /**
    * At set end.
    * Save the score at this point.
    *
    * @param team1Score
    * team1's score.
    * @param team2Score
    * team2's score.
    */
  override def endSet(team1Score: Int, team2Score: Int): Unit = {
    sets += GameSet(currentSet.playersHand, currentSetHands, currentSet.briscola, team1Score, team2Score)
  }

  /**
    * At game ending.
    * Save the winners.
    *
    * @param winners
    * game's winners.
    */
  override def endGame(winners: Seq[String]): Unit = {
    val game: Game = Game(players, sets, winners)

    RedisGameUtils().saveGame(gameId, game)
  }
}