package it.unibo.pps2017.core.game

import it.unibo.pps2017.client.model.actors.ClientGameActor
import it.unibo.pps2017.core.deck.cards.{Card, Seed}


trait Match {

  /**
    * Starting the game.
    */
  def startGame(): Unit

  /**
    * Check if all operations concerning the previous set are closed.
    * If it's all right, it shuffle the deck and start a new set.
    */
  def playSet(): Unit

  /**
    * Setting the briscola for the current set.
    *
    * @param seed
    * Current briscola's seed.
    */
  def setBriscola(seed: Seed.Seed): Unit

  /**
    * Check if the played card is accepted.
    * The card may be refused if it is not the current suit but the player has one in his hand
    *
    * @param card
    * Played card.
    * @param player
    * The played that played the card
    */
  def isCardOk(card: Card, player: String): Unit

  /**
    * Play a random card in the hand of the player.
    *
    * @param player
    * Reference player.
    * @return
    * A random card among those that the player can drop.
    */
  def forcePlay(player: String): Card

  /**
    * If set is end return the score of teams, and a true if the game is end, false otherwise.
    *
    * @return
    * If set is end return the score of teams, and a true if the game is end, false otherwise.
    */
  def isSetEnd: Option[(Int, Int, Boolean)]
}
