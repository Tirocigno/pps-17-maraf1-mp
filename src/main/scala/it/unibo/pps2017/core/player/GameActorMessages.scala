package it.unibo.pps2017.core.player

import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer


//#messages
final case class RegisterPlayer(player: PlayerActor)
final case class DistributedCard(cards: List[String],player: PlayerActor)
final case class PlayersRef(players: ListBuffer[PlayerActor])
final case class Turn(player: PlayerActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)
final case class SelectBriscola(player: PlayerActor)
final case class BriscolaChosen(seed: Seed)
final case class NotifyBriscolaChosen(seed: Seed)
final case class ForcedCardPlayed(card: String, player: PlayerActor)
final case class ClickedCard(index: Int, playerActor: PlayerActor)
final case class CardOk(correctClickedCard: Boolean)
final case class PlayedCard(card: String, player: PlayerActor)
final case class ClickedCommand(command: String, player: PlayerActor)
final case class NotifyCommandChosen(command: String, player: PlayerActor)
//#messages


