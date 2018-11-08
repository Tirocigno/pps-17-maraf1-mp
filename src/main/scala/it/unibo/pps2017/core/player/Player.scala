package it.unibo.pps2017.core.player

import akka.actor.ActorRef
import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed


/**
  * This trait define the concept of player, who has a username.
  */
object Player{
  val TURN_TIME_SEC: Int = 10
  val TIME_PERIOD: Long = 1000L
}

trait Player {

  def userName: String

  def playerRef : ActorRef

  /**
    * Called initially when the cards are shuffled and distributed to
    * set each player's hand
    *
    * @param cards  set of cards
    */

  def setHand(cards: Set[Card]): Unit
  /**
    * Returns the cards in hand of a specific player
    *
    * @return the cards that the player has
    */

  def getHand(): Set[Card]

  /**
    * Returns the card at a specific index
    * @param index of the card
    * @return the card
    */

  def getCardAtIndex(index : Int): Card

  /**
    * Method called when the player has the turn to play the card
    */
  def onMyTurn(): Unit

  /**
    * Notify the player to select a seed for briscola
    * @return the seed chosen
    */
  def onSetBriscola(): Seed

}
