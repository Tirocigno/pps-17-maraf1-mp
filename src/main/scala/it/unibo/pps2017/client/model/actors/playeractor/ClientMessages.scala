package it.unibo.pps2017.client.model.actors.playeractor

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer

object ClientMessages {

  /**
    * Message to inform player of four players of certain match.
    *
    * @param playersList Four players list.
    */
  case class PlayersRef(playersList: ListBuffer[String]) extends ActorMessage

  /**
    * Message to confirm receipt of players' list.
    */
  case class PlayersRefAck() extends ActorMessage

  /**
    * Message to send to player cards' list of his hand.
    *
    * @param cards  List that contains path's cards of player's hand.
    * @param player Player to whom the cards are addressed.
    */
  case class DistributedCard(cards: List[String], player: String) extends ActorMessage

  /**
    * Message to inform player that must be choose briscola.
    *
    * @param player Player.
    */
  case class SelectBriscola(player: String) extends ActorMessage

  /**
    * Message to inform of briscola chosen.
    *
    * @param seed Seed of briscola chosen.
    */
  case class BriscolaChosen(seed: Seed) extends ActorMessage

  /**
    * Message to inform all players of briscola chosen.
    *
    * @param seed Seed of briscola chosen.
    */
  case class NotifyBriscolaChosen(seed: Seed) extends ActorMessage

  /**
    * Message to inform all players of actual turn.
    *
    * @param player         This player must be plays a card.
    * @param endPartialTurn True if partial turn is ended, false otherwise.
    * @param isFirstPlayer  True if player is the first player of turn, false otherwise.
    * @param isReplay       True if actor is a replay actor, false otherwise.
    */
  case class Turn(player: String, endPartialTurn: Boolean, isFirstPlayer: Boolean, isReplay: Boolean) extends ActorMessage

  /**
    * Message to inform GameActor of clicked card.
    *
    * @param index  Index of clicked card.
    * @param player Player who clicked card.
    */
  case class ClickedCard(index: Int, player: String) extends ActorMessage

  /**
    * Message to inform player of clicked card.
    *
    * @param index Index of clicked card.
    */
  case class ClickedCardActualPlayer(index: Int) extends ActorMessage

  /**
    * Message to inform all players of clicked card and from who player.
    *
    * @param card   Played card.
    * @param player Player who played the card.
    */
  case class PlayedCard(card: String, player: String) extends ActorMessage

  /**
    * Message to inform GameActor of clicked command.
    *
    * @param command Command clicked from player.
    * @param player  Player who clicked command.
    */
  case class ClickedCommand(command: String, player: String) extends ActorMessage

  /**
    * Message to inform player of clicked command.
    *
    * @param command Command clicked from player.
    */
  case class ClickedCommandActualPlayer(command: String) extends ActorMessage

  /**
    * Message to inform all players of clicked command.
    *
    * @param command Command clicked from player.
    * @param player  Player who clicked command.
    */
  case class NotifyCommandChosen(command: String, player: String) extends ActorMessage

  /**
    * Message to inform player if clicked card is ok or not.
    *
    * @param correctClickedCard True if clicked card is ok, false otherwise.
    * @param player             Player who clicked card.
    */
  case class CardOk(correctClickedCard: Boolean, player: String) extends ActorMessage

  /**
    * Message to inform controller of situation at the end of set/total match.
    *
    * @param winner1  First of two members of the winner team.
    * @param winner2  Second of two members of the winner team.
    * @param score1   First team score.
    * @param score2   Second team score.
    * @param endMatch True if match is ended, false otherwise.
    */
  case class GameScore(winner1: String, winner2: String, score1: Int, score2: Int, endMatch: Boolean) extends ActorMessage

  /**
    * Message to register player to topic of game.
    *
    * @param id Topic's ID.
    */
  case class IdChannelPublishSubscribe(id: String) extends ActorMessage

  /**
    * Message to inform GameActor of received NotifyBriscolaChosen's message.
    */
  case class BriscolaAck() extends ActorMessage

  /**
    * Message to inform GameActor of received PlayedCard's message.
    */
  case class CardPlayedAck() extends ActorMessage

  /**
    * Message to inform controller of situation at the end of set/total match.
    *
    * @param player   Represent player of PlayerActorClient.
    * @param winner1  First of two members of the winner team.
    * @param winner2  Second of two members of the winner team.
    * @param score1   First team score.
    * @param score2   Second team score.
    * @param endMatch True if match is ended, false otherwise.
    */
  case class ComputeGameScore(player: String, winner1: String, winner2: String, score1: Int, score2: Int, endMatch: Boolean) extends ActorMessage

  /**
    * Message to set username of player.
    *
    * @param playerUsername Username chosen by player.
    */
  case class SetUsernamePlayer(playerUsername: String) extends ActorMessage

  /**
    * Message to inform a Viewer of actual situation of match selected to view.
    *
    * @param playersList List of four players in game.
    * @param cards       Remaining cards of first user hand.
    * @param seed        Actual briscola chosen.
    * @param player      First player that viewer represents.
    */
  case class RecapActualSituation(playersList: ListBuffer[String], cards: ListBuffer[String], seed: Seed, player: String) extends ActorMessage

  /**
    * Message to inform GameActor of abandonment of match.
    *
    * @param player Player who abandonment match.
    */
  case class ClosedPlayGameView(player: String) extends ActorMessage

  /**
    * Message to inform player of abandonment of match.
    */
  case class NotifyClosedPlayGameView() extends ActorMessage

}
