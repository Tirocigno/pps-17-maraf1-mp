package it.unibo.pps2017.core.player

import java.util

import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.core.game.Match

abstract class PlayerManager(model:Match) extends Controller{

  var allCardsInHand = Map[Player, List[Card]]()
  //allCardsInHand += ("User1" -> null, "User2" -> null, "User3" -> null, "User4" -> null)
  var playerTurn : Player

  /**
    * Get all the cards that each player actually has
    *
    * @return cards that players have
    */
  override def getAllHands: Map[Player,List[Card]] =  allCardsInHand


  /**
    * Called initially when the cards are shuffled and distributed to
    * set each player's hand
    *
    * @param hand  list of all the cards
    */
  override def setHands(hand: util.List[Card]): Unit = {

  }

  /**
    * Returns the cards in hand of a specific player
    *
    * @param player  the player
    * @return the cards that the player has
    */
  override def getPlayerHand(player: Player): Option[List[Card]] = allCardsInHand.get(player)

  /**
    * Check if the selected and played card can be played
    *
    * @param card   the card.
    * @return true if the card can be played, otherwise false
    */
  override def isCardOk(card: Card): Boolean = {
    if(model.isCardOk(card)){
      //gui.showOtherPlayersPlayedCard(playerTurn,cardPath)
    }
    true
  }

  /**
    * Called by model to set the actual turn
    *
    * @param player  the player who has the turn
    */
  override def setTurn(player: Player): Unit = {
    playerTurn = player
    //gui.setCurrentPlayer(playerTurn)
  }

  /**
    * Called initially when the player enter the game
    *
    * @param player  the player to be added to the game
    */
  override def addPlayer(player: Player): Unit = {
    //model.addPlayer(player,"User1")
  }

  /**
    * To be called when each player has played one card which
    * means the round is finished, so it's necessary to update
    * the view
    */
  override def cleanField(): Unit = {
    //gui.cleanField(playerTurn)
  }


  /**
    * To be called when the time of the turn of one player has
    * expired so he will play a random card from his hand
    * considering the game rules
    *
    * @return the 'almost' random card
    */
  override def getRandCard: Card = {
    //Card card = model.getRandCardToPlay(allCardsInHand.get(playerTurn))
    //gui.playedCard(card)
    null
  }

  /**
    * Useful to notify all the players when one of them choose a
    * command which can be: busso, striscio, volo
    *
    * @param command  the command choosen from the player
    */
  override def setCommand(command: String): Unit = {
    //model.setCommand(command)
  }

  /**
    * Method to be called when the game is finished, so the view
    * can show up the points of each tem
    *
    * @param pointsTeam1  the points of the first team
    * @param pointsTeam2  the points of the second team
    */
  override def totalPoints(pointsTeam1: Integer, pointsTeam2: Integer): Unit = {
    //gui.showTotalPoints(pointsTeam1,pointsTeam2)
  }

  //override def timeExpired(): Boolean = ???
}
