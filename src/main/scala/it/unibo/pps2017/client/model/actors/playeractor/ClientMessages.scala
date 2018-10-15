package it.unibo.pps2017.client.model.actors.playeractor

import it.unibo.pps2017.client.model.actors.{ActorMessage, ClientGameActor}
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer

object ClientMessages {

  case class PlayersRef(playersList: ListBuffer[ClientGameActor]) extends ActorMessage

  case class DistributedCard(cards: List[String], player: ClientGameActor) extends ActorMessage

  case class SelectBriscola(player: ClientGameActor) extends ActorMessage

  case class BriscolaChosen(seed: Seed) extends ActorMessage

  case class NotifyBriscolaChosen(seed: Seed) extends ActorMessage

  case class Turn(player: ClientGameActor, endPartialTurn: Boolean, isFirstPlayer: Boolean) extends ActorMessage

  case class ClickedCard(index: Int, player: ClientGameActor) extends ActorMessage

  case class PlayedCard(card: String, player: ClientGameActor) extends ActorMessage

  case class ClickedCommand(command: String, player: ClientGameActor) extends ActorMessage

  case class NotifyCommandChose(command: String, player: ClientGameActor) extends ActorMessage

  case class ForcedCardPlayed(card: String, player: ClientGameActor) extends ActorMessage

  case class CardOk(correctClickedCard: Boolean) extends ActorMessage

  case class SetTimer(timer: Int) extends ActorMessage

  case class PartialGameScore(winner1: ClientGameActor, winner2: ClientGameActor, score1: Int, score2: Int) extends ActorMessage

  case class FinalGameScore(winner1: ClientGameActor, winner2: ClientGameActor, score1: Int, score2: Int) extends ActorMessage

  case class IdChannelPublishSubscribe(id: String) extends ActorMessage

  case class BriscolaAck() extends ActorMessage

  case class CardPlayedAck() extends ActorMessage

}
