
package it.unibo.pps2017.core.deck.cards

/**
  * This object implement the concept of Seed of a card, there are only four possible seed for card.
  */

object Seed {

  sealed trait Seed {
    var seed: String = _

    def getSeed(): String = this.seed

    def setSeed(seed: String): Unit = this.seed = seed
  }

  case object Sword extends Seed

  case object Cup extends Seed

  case object Coin extends Seed

  case object Club extends Seed



  /**
    * This method is used to get all the available seeds
    *
    * @return a Iterable containing all the available seeds.
    */
  def values: Iterable[Seed] = Iterable(Sword, Cup, Coin, Club)

}
