package it.unibo.pps2017.core.player

import java.util

import it.unibo.pps2017.core.deck.cards.{Card, Seed}
import it.unibo.pps2017.core.game.Match

abstract class PlayerManager(model:Match) extends Controller{

  var allCardsInHand = Map[String, List[Card]]()
  allCardsInHand += ("User1" -> null, "User2" -> null, "User3" -> null, "User4" -> null)
  var playerTurn : String

  override def getAllHands: Map[String,List[Card]] =  allCardsInHand


  override def setHands(hand: util.List[Card]): Unit = {

  }

  override def getPlayerHand(player: String): Option[List[Card]] = allCardsInHand.get(player)

  override def isCardOk(card: Card): Boolean = {
    if(model.isCardOk(card)){
      //gui.showOtherPlayersPlayedCard(playerTurn,cardPath)
    }
    true
  }

  override def setTurn(player: String): Unit = {
    playerTurn = player
    //gui.setCurrentPlayer(playerTurn)
  }


  override def addPlayer(c: Controller): Unit = {
    model.addPlayer(c,"User1")
  }

  override def cleanField(): Unit = {
    //gui.cleanField(playerTurn)
  }


  override def getRandCard: Card = {
    //Card card = model.getRandCardToPlay(allCardsInHand.get(playerTurn))
    //gui.playedCard(card)
    null
  }

  override def setCommand(command: String): Unit = {
    //model.setCommand(command)
  }

  override def totalPoints(pointsTeam1: Integer, pointsTeam2: Integer): Unit = {
    //gui.showTotalPoints(pointsTeam1,pointsTeam2)
  }

  //override def timeExpired(): Boolean = ???
}
