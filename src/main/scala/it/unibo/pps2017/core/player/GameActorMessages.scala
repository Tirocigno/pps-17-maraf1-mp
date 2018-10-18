package it.unibo.pps2017.core.player

import it.unibo.pps2017.client.model.actors.ClientGameActor
import it.unibo.pps2017.core.deck.cards.Seed.Seed

import scala.collection.mutable.ListBuffer


//#messages
final case class PlayersRefAck()

final case class DistributedCard(cards: List[String], player: ClientGameActor)
final case class PlayersRef(players: ListBuffer[String])

final case class Turn(player: ClientGameActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)

final case class SelectBriscola(player: ClientGameActor)
final case class BriscolaChosen(seed: Seed)
final case class BriscolaAck()
final case class NotifyBriscolaChosen(seed: Seed)

final case class ForcedCardPlayed(card: String, player: ClientGameActor)

final case class ClickedCard(index: Int, playerActor: ClientGameActor)
final case class PlayedCardAck()

final case class CardOk(correctClickedCard: Boolean, playerActor: ClientGameActor)

final case class PlayedCard(card: String, player: ClientGameActor)

final case class ClickedCommand(command: String, player: ClientGameActor)

final case class NotifyCommandChosen(command: String, player: ClientGameActor)

final case class PartialGameScore(winner1: ClientGameActor, winner2: ClientGameActor, score1: Int, score2: Int)

final case class FinalGameScore(winner1: ClientGameActor, winner2: ClientGameActor, score1: Int, score2: Int)
//#messages


