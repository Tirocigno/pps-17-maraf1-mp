package it.unibo.pps2017.core.player

import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.core.game.Match
import it.unibo.pps2017.core.player.PlayerManager._

import scala.collection.mutable.{ListBuffer, Seq, Map}

object PlayerManager{
  val IMG_PATH = "src/main/java/it/unibo/pps2017/core/gui/cards/"
  val PNG_FILE = ".png"
  def apply(model:Match) = new PlayerManager(model)
}


 class PlayerManager(model:Match) extends Controller{

  var allCardsInHand : Map[Player,ListBuffer[Card]] = Map[Player,ListBuffer[Card]]()
  var playerTurn : Player = _
  var turnBriscola : Player = _
  var currentPlayerCommand : Player = _
  var totHandsSet : Int = 0
  var players : Seq[Player] = Seq[Player]()

  /**
    * Get all the cards that each player actually has
    *
    * @return cards that players have
    */
  override def getAllHands: Map[Player,ListBuffer[Card]] =  allCardsInHand

  /**
    * Checks if all the players has got the card in way to update the view
    */
  override def incSetHands(): Unit = {
    totHandsSet = totHandsSet + 1
    if(totHandsSet == 4){
      var allCardsPath : ListBuffer[String] = ListBuffer[String]()
      players.foreach(p => p.getHand().foreach(c =>
        allCardsPath += IMG_PATH + c.cardValue + c.cardSeed + PNG_FILE
      ))
      totHandsSet = 0
      //gui.setCardsPath(allCardsPath)
    }
  }

  /**
    * Update the cards of the player's hand on the view
    * @param cardsPath list of cards images path
    */
  override def setHandView(cardsPath: Set[Card]): Unit = {
    var allCardsPath : ListBuffer[String] = ListBuffer[String]()
    cardsPath.foreach(card =>
      allCardsPath += IMG_PATH + card.cardValue + card.cardSeed + PNG_FILE
    )
    //gui.getCardsFirstPlayer(allCardsPath)
  }

  /**
    * Checks if it's his turn for briscola
    * @param player the player
    * @return true if is his turn else false
    */
  override def isMyTurnToChooseBriscola(player: Player): Boolean = turnBriscola.equals(player)

  /**
    * Called to set briscola
    * @param seed the seed chosen
    */

  override def setMyBriscola(seed: Seed): Unit = {
    //model.setBriscola(seed)
  }

  /**
    * Called to set the called command and the player who called it
    * @param command the command chosen
    * @param player the player who called the command
    */

  override def setCommandFromPlayer(command: Command.Command, player: Player): Unit = {
    currentPlayerCommand = player
    //model.setCommand(command,player)
  }

    /**
    * Check if the selected and played card can be played
    *
    * @param cardIndex  index of the card.
    * @return true if the card can be played, otherwise false
    */
  override def isCardOk(cardIndex: Int): Boolean = {
    var playedCard: Card = playerTurn.getCardAtIndex(cardIndex)
    if(model.isCardOk(playedCard)){
      val cardPath = IMG_PATH + playedCard.cardValue + playedCard.cardSeed + PNG_FILE
      //gui.showOtherPlayersPlayedCard(playerTurn,cardPath)
      true
    }
    false
  }

  /**
    * Called by model to set the actual turn
    *
    * @param player  the player who has the turn
    */
  override def setTurn(player: Player): Unit = {
    /*
       if(model.isSetEnd()._3) totalPoints(model.isSetEnd()._1,model.isSetEnd()._2)
       else gui.setCurrentPlayer(player, false)
      */
    /* OR
       gui.setCurrentPlayer(player, model.isSetEnd()._3)
      */
    playerTurn = player
  }

  /**
    * Called initially when the player enter the game
    *
    * @param player  the player to be added to the game
    */
  override def addPlayer(player: Player): Unit = {
    allCardsInHand += (player -> ListBuffer[Card]())
    players :+ player
    //firstPlayer = gui
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
    */
  override def getRandCard: Unit = {
    //val card : Card = model.forcePlay(playerTurn)
    //val cardPath = IMG_PATH + card.cardValue + card.cardSeed +PNG_FILE
    //gui.showOtherPlayersPlayedCard(playerTurn,cardPath)
  }

  /**
    * Useful to notify all the players when one of them choose a
    * command which can be: busso, striscio, volo
    *
    * @param command  the command chosen from the player
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
    //gui.showAnimationEndMatch(pointsTeam1,pointsTeam2)
  }

  /**
    * To check if it's player's turn or not
    * @param player the player
    */
  override def isPlayerTurn(player: Player): Unit = playerTurn.equals(player)

}
