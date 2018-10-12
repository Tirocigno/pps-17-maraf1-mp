
package it.unibo.pps2017.core.deck.cards

/**
  * This object implement the concept of Seed of a card, there are only four possible seed for card.
  */

object Seed {

  sealed trait Seed {
    def asString:String
  }

  case object Sword extends Seed {
    override val asString: String = "Sword"
  }

  case object Cup extends Seed {
    override val asString: String = "Cup"
  }

  case object Coin extends Seed {
    override val asString: String = "Coin"
  }

  case object Club extends Seed {
    override val asString: String = "Club"
  }



  /**
    * This method is used to get all the available seeds
    *
    * @return a Iterable containing all the available seeds.
    */
  def values: Iterable[Seed] = Iterable(Sword, Cup, Coin, Club)

}
