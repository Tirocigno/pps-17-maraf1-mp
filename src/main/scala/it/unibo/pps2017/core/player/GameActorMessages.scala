package it.unibo.pps2017.core.player

import it.unibo.pps2017.core.deck.cards.Seed.Seed
import it.unibo.pps2017.core.game.Team

import scala.collection.mutable.ListBuffer


//#messages
final case class RegisterPlayer(team1: Team, team2: Team)
final case class DistributedCard(cards: List[String],player: PlayerActor)
final case class PlayersRef(players: ListBuffer[String])
final case class Turn(player: PlayerActor, endPartialTurn: Boolean, isFirstPlayer: Boolean)
final case class SelectBriscola(player: PlayerActor)
final case class BriscolaChosen(seed: Seed)
final case class BriscolaAck()
final case class NotifyBriscolaChosen(seed: Seed)
final case class ForcedCardPlayed(card: String, player: PlayerActor)
final case class ClickedCard(index: Int, playerActor: PlayerActor)
final case class PlayedCardAck()
final case class CardOk(correctClickedCard: Boolean, playerActor: PlayerActor)
final case class PlayedCard(card: String, player: PlayerActor)
final case class ClickedCommand(command: String, player: PlayerActor)
final case class NotifyCommandChosen(command: String, player: PlayerActor)
final case class PartialGameScore(winner1: PlayerActor, winner2: PlayerActor, score1: Int, score2: Int)
final case class FinalGameScore(winner1: PlayerActor, winner2: PlayerActor, score1: Int, score2: Int)
//#messages


