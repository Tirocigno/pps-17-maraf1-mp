
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.game.Team

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

  override def registerTurnPlayedCards(playedCards: Seq[Card], team: Team): Unit =
    scoreCounter.registerSetPlayedCards(playedCards, team)

  override def distribute(): Seq[Set[Card]] = simpleDeck.distribute()

  override def registerMarafona(team: Team): Unit = scoreCounter registerMarafona team
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


