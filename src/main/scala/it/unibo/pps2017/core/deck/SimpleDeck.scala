
package it.unibo.pps2017.core.deck

/**
  * Trait to implement a simple deck used in a game.
  * A Deck is a collection of 40 different cards that can be shuffled, split in
  * four different hands of card.
  */
trait SimpleDeck {

  /**
    * This method shuffle the order of the cards inside the deck.
    */
  def shuffle(): Unit

  /**
    * Split the deck of card inside in four random collections of cards of the same size.
    *
    * @return
    */
  def distribute(): Seq[CardsHand]
}
