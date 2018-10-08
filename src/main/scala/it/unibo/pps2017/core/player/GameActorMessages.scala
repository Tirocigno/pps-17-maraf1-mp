package it.unibo.pps2017.core.player

import akka.actor.ActorRef
import it.unibo.pps2017.core.deck.cards.Card
import it.unibo.pps2017.core.deck.cards.Seed.Seed


//#messages
final case class RegisterPlayer(player: Player)
final case class DistributedCard(cards: Set[Card],player: ActorRef)
final case class Turn(player: ActorRef, endPartialTurn: Boolean, isFirstPlayer: Boolean)
final case class SelectBriscola(player: ActorRef)
final case class BriscolaChosen(seed: Seed)
final case class NotifyBriscolaChosen(seed: Seed, player: Player)
final case class ClickedCard(index: Int)
final case class ClickedCommand(command: String)
//#messages


