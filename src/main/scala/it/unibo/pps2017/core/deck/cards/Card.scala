
package it.unibo.pps2017.core.deck.cards

import it.unibo.pps2017.core.deck.cards.Seed.Seed

/**
  * This trait define the basic behaviour of a card.
  * A card has a seed and a value.
  */

sealed trait Card {

  private[this] val minValue = 4

  def cardSeed: Seed

  def cardValue: Int

  /**
    * This method is used to check if the value on a card is greater than the on on another.
    *
    * @param otherCard the other card to compare.
    * @return true if the value on the card is greater than the other one, false otherwise.
    */
  //noinspection ScalaStyle
  def >(otherCard: Card): Boolean = otherCard match {
    case CardImpl(_, otherValue) if otherValue < minValue && cardValue < minValue => cardValue > otherValue
    case CardImpl(_, otherValue) if otherValue < minValue && cardValue >= minValue => false
    case CardImpl(_, otherValue) if otherValue >= minValue && cardValue < minValue => true
    case CardImpl(_, otherValue) if otherValue >= minValue && cardValue >= minValue => cardValue > otherValue
  }

  /**
    * This method is used to check if the value on a card is lower than the on on another.
    *
    * @param otherCard the other card to compare
    * @return true if the value on the card is lower than the other one, false otherwise.
    */
  //noinspection ScalaStyle
  def <(otherCard: Card): Boolean = otherCard match {
    case CardImpl(_, otherValue) if cardValue == otherValue => false
    case _ => ! >(otherCard)
  }

  override def equals(obj: Any): Boolean = obj match {
    case CardImpl(seed, value) if cardSeed.equals(seed) && cardValue == value => true
    case _ => false
  }

  override def hashCode(): Int = super.hashCode()
}

/**
  * Basic implementation of card.
  *
  * @param cardSeed  the seed of the card.
  * @param cardValue the value of the card.
  */
case class CardImpl(override val cardSeed: Seed, override val cardValue: Int) extends Card
