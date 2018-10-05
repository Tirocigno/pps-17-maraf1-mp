
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.Card

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

  override def registerTurnPlayedCards(playedCards: Seq[Card], teamIndex: Int): Unit =
    scoreCounter.registerSetPlayedCards(playedCards, teamIndex)

  override def distribute(): Seq[Iterable[Card]] = simpleDeck.distribute()
}

/**
  * Companion object for ComposedDeck.
  */
object ComposedDeck {

  /**
    * Apply method used as default constructor for the ComposedDeck class.
    *
    * @return a Deck with 40 cards and a ScoreCounter set to zero.
    */
  def apply(): ComposedDeck = new ComposedDeck(SimpleDeck(), ScoreCounter())
}


