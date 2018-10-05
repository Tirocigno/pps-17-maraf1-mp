package it.unibo.pps2017.core.player

import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


/**
  * This trait define the concept of player, who has a username.
  */

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
    * @return the seed choosen
    */
  def onSetBriscola(): Seed

  def getFuture(): Future[String]

}

/**
  * Basic implementation of player.
  *
  * @param userName  the username of the player.
  */
case class PlayerImpl(override val userName: String) extends Player {

  var cardList : Set[Card] = _
  //var controller : PlayerManager
  var timer : Future[String] = _

  override def equals(obj: Any): Boolean = obj match {
    case PlayerImpl(username) if userName.equals(username) => true
    case _ => false
  }

  override def hashCode(): Int = super.hashCode()

  override def setHand(cards: Set[Card]): Unit = {
    cardList = cards
    //controller.incSetHands()
  }

  override def getHand(): Set[Card] = cardList

  override def getFuture(): Future[String] = timer

  override def onMyTurn(): Unit = {
    //controller.setTurn(this)

    timer = Future {
      //controller.updateTimer()
      Thread.sleep(10000)
      "Nothing played"
    }

    timer.onComplete {
      case Success(value) => //controller.getRandCard()
      case Failure(e) => e.printStackTrace
    }
  }

  override def onSetBriscola(): Seed = ???

  override def getCardAtIndex(index: Int): Card = {
    val mySeq = cardList.toSeq
    mySeq(index)
  }

}
