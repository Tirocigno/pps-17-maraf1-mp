
package it.unibo.pps2017.core.deck

import java.util

import it.unibo.pps2017.core.deck.ComposedDeck.scalaToJavaHandConversion
import it.unibo.pps2017.core.deck.cards.Card

import scala.collection.JavaConverters._
import scala.language.implicitConversions

/**
  * This class is the core of all the deck package: implements the interface GameDeck composing a SimpleDeck and a
  * ScoreCounter object.
  *
  * @param simpleDeck   an object extending the SimpleDeck trait.
  * @param scoreCounter an object extending the ScoreCounter trait.
  */
class ComposedDeck(val simpleDeck: SimpleDeck, val scoreCounter: ScoreCounter) extends GameDeck {

  override def shuffle(): Unit = simpleDeck shuffle()

  override def computeSetScore(): (Int, Int) = scoreCounter.computeSetScore()

  override def registerTurnPlayedCards(playedCards: util.List[Card], teamIndex: Int): Unit =
    scoreCounter.registerSetPlayedCards(playedCards.asScala, teamIndex)

  override def distribute(): util.List[util.Collection[Card]] = simpleDeck.distribute()
}

/**
  * Companion object for ComposedDeck.
  */
object ComposedDeck {

  /**
    * Implicit method to convert a Scala Seq to a Java list.
    *
    * @param seq the scala sequence to convert.
    * @return a java list containing some Java Collections of cards.
    */
  implicit def scalaToJavaHandConversion(seq: Seq[CardsHand]): util.List[util.Collection[Card]] =
    seq.toStream.map(_.asJavaCollection).asJava

  /**
    * Apply method used as default constructor for the ComposedDeck class.
    *
    * @return a Deck with 40 cards and a ScoreCounter set to zero.
    */
  def apply(): ComposedDeck = new ComposedDeck(SimpleDeck(), ScoreCounter())
}


