package it.unibo.pps2017.core.player

import akka.actor.ActorRef
import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed


//#messages
final case class RegisterPlayer(player: PlayerActor)
final case class DistributedCard(cards: Set[Card],player: ActorRef)
final case class Turn(player: ActorRef, endPartialTurn: Boolean, isFirstPlayer: Boolean)
final case class SelectBriscola(player: ActorRef)
final case class BriscolaChosen(seed: Seed)
final case class NotifyBriscolaChosen(seed: Seed)
final case class ForcedCardPlayed(card: Card, player: PlayerActor)
final case class ClickedCard(index: Int, playerActor: PlayerActor)
final case class CardOk(correctClickedCard: Boolean)
final case class PlayedCard(card: Card, player: PlayerActor)
final case class ClickedCommand(command: String, player: PlayerActor)
final case class NotifyCommandChosen(command: String, player: PlayerActor)
//#messages


