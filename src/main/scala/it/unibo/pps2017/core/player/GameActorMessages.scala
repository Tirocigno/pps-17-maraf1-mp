package it.unibo.pps2017.core.player

import it.unibo.pps2017.core.deck.cards.Card


//#messages
final case class RegisterPlayer(player: Player)
final case class DistributedCard(cards: Set[Card])
final case class ClickedCard(index: Int)
final case class ClickedCommand(command: String)
//#messages


