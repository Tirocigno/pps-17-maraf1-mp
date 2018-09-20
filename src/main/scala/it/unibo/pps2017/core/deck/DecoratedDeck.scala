
package it.unibo.pps2017.core.deck

import java.util

import it.unibo.pps2017.core.deck.cards.Card

import scala.collection.JavaConverters._

class DecoratedDeck(val simpleDeck: SimpleDeck, val scoreCounter: ScoreCounter) extends GameDeck {

  override def shuffle(): Unit = simpleDeck shuffle()

  override def computeSetScore(): (Int, Int) = scoreCounter.scores

  override def registerTurnPlayedCards(playedCards: util.List[Card], teamIndex: Int): Unit =
    scoreCounter.registerSetPlayedCards(playedCards.asScala, teamIndex)

  override def distribute(): util.List[util.Collection[Card]] = simpleDeck.distribute()

}


