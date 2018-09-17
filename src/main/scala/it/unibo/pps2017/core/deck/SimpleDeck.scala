
package it.unibo.pps2017.core.deck

import it.unibo.pps2017.core.deck.cards.{Card, CardImpl, Seed}

/**
  * Trait to implement a simple deck used in a game.
  * A Deck is a collection of 40 different cards that can be shuffled, split in
  * four different hands of card.
  */
trait SimpleDeck {

  private[deck] var cardList: Seq[Card]

  /**
    * This method shuffle the order of the cards inside the deck.
    */
  def shuffle(): Unit

  /**
    * Split the deck of card inside in four random collections of cards of the same size.
    *
    * @return a sequence containing four hands of cards.
    */
  def distribute(): Seq[CardsHand]
}

/**
  * Companion object for SimpleDeck trait.
  */
object SimpleDeck {
  def apply(): SimpleDeck = {
    val cardList = Seed.values.toStream.flatMap(seed => Stream.range(1, 10).map(CardImpl(seed, _)))
    new SimpleDeckImpl(cardList)
  }
}


private class SimpleDeckImpl(override var cardList: Seq[Card]) extends SimpleDeck {

  override def shuffle(): Unit = cardList.shuffle()

  override def distribute(): Seq[CardsHand] = ???
}
