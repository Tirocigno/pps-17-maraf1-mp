package it.unibo.pps2017.core.player

import java.util.TimerTask

import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.game.Match
import it.unibo.pps2017.core.player.Player._


/**
  * This trait define the concept of player, who has a username.
  */
object Player{
  val TURN_TIME_SEC: Int = 10
  val TIME_PERIOD: Long = 1000L
}

 trait Player {

  def userName: String

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

/**
  * Basic implementation of player.
  *
  * @param userName  the username of the player.
  */
  case class PlayerImpl(override val userName: String) extends Player {

  var cardList : Set[Card] = Set[Card]()
  var controller : PlayerManager = PlayerManager(Match)
  var cardPlayed : Boolean = false
  var task : TimerTask = TimerTask

  override def equals(obj: Any): Boolean = obj match {
    case PlayerImpl(username) if userName.equals(username) => true
    case _ => false
  }

  override def hashCode(): Int = super.hashCode()

  override def setHand(cards: Set[Card]): Unit = {
    cardList = cards
    controller.setHandView(cards)
  }

  override def getHand(): Set[Card] = cardList

  override def onMyTurn(): Unit = {
    controller.setTurn(this)
    var tmp = 0
    val timer = new java.util.Timer()
    task = new java.util.TimerTask {
      def run() = {
        tmp = tmp + 1
        cardPlayed match {
          case true => {
            cardPlayed = false
            endTask()
          }
          case false => {
            if(tmp == TURN_TIME_SEC) controller.getRandCard()
          }
        }
      }
    }

    timer.schedule(task, TIME_PERIOD, TIME_PERIOD)
  }

  def endTask(): Unit ={
    task.cancel()
  }

  override def onSetBriscola(): Seed = {
    //gui.getSeedForBriscola()
    //model.setBriscola()
    null
  }

  override def getCardAtIndex(index: Int): Card = {
    val mySeq = cardList.toSeq
    mySeq(index)
  }

}

