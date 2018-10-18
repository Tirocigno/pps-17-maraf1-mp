package it.unibo.pps2017.core.player

import it.unibo.pps2017.client.model.actors.ClientGameActor
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer


//#messages
final case class PlayersRefAck()
final case class DistributedCard(cards: List[String], player: String)
final case class PlayersRef(players: ListBuffer[String])
final case class Turn(player: String, endPartialTurn: Boolean, isFirstPlayer: Boolean)
final case class SelectBriscola(player: String)
final case class BriscolaChosen(seed: Seed)
final case class BriscolaAck()
final case class NotifyBriscolaChosen(seed: Seed)
final case class ForcedCardPlayed(card: String, player: String)
final case class ClickedCard(index: Int, player: String)
final case class PlayedCardAck()
final case class CardOk(correctClickedCard: Boolean, player: String)
final case class PlayedCard(card: String, player: String)
final case class ClickedCommand(command: String, player: String)
final case class NotifyCommandChosen(command: String, player: String)
final case class PartialGameScore(winner1: String, winner2: String, score1: Int, score2: Int)
final case class FinalGameScore(winner1: String, winner2: String, score1: Int, score2: Int)
//#messages


