
package it.unibo.pps2017.client.model.actors.playeractor

import it.unibo.pps2017.client.model.actors.ActorMessage
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer

object ClientMessages {

  case class PlayersRef(playersList: ListBuffer[String]) extends ActorMessage

  case class PlayersRefAck() extends ActorMessage

  case class DistributedCard(cards: List[String], player: String) extends ActorMessage

  case class SelectBriscola(player: String) extends ActorMessage

  case class BriscolaChosen(seed: Seed) extends ActorMessage

  case class NotifyBriscolaChosen(seed: Seed) extends ActorMessage

  case class Turn(player: String, endPartialTurn: Boolean, isFirstPlayer: Boolean) extends ActorMessage

  case class ClickedCard(index: Int, player: String) extends ActorMessage

  case class ClickedCardActualPlayer(index: Int) extends ActorMessage

  case class PlayedCard(card: String, player: String) extends ActorMessage

  case class ClickedCommand(command: String, player: String) extends ActorMessage

  case class ClickedCommandActualPlayer(command: String) extends ActorMessage

  case class NotifyCommandChosen(command: String, player: String) extends ActorMessage

  case class CardOk(correctClickedCard: Boolean, player: String) extends ActorMessage

  case class GameScore(winner1: String, winner2: String, score1: Int, score2: Int, endMatch: Boolean) extends ActorMessage
  case class PartialGameScore(winner1: String, winner2: String, score1: Int, score2: Int) extends ActorMessage
  case class FinalGameScore(winner1: String, winner2: String, score1: Int, score2: Int) extends ActorMessage

  case class IdChannelPublishSubscribe(id: String) extends ActorMessage

  case class BriscolaAck() extends ActorMessage

  case class CardPlayedAck() extends ActorMessage

  case class ComputePartialGameScore(user: String, winner1: String, winner2: String, score1: Int, score2: Int) extends ActorMessage

  case class ComputeFinalGameScore(user: String, winner1: String, winner2: String, score1: Int, score2: Int) extends ActorMessage

  case class SetUsernamePlayer(playerUsername: String) extends ActorMessage

  case class RecapActualSituation(playersList: ListBuffer[String], cards: ListBuffer[String], seed: Seed, player: String) extends ActorMessage

  case class ClosedPlayGameView(player: String) extends ActorMessage
}
