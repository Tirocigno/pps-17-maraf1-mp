package it.unibo.pps2017.core.messages

import it.unibo.pps2017.core.player.Player

class PlayerActorMessages {

  final case class DistributedCardMsg (cards: List[String])
  final case class SelectBriscolaMsg()
  final case class TurnMsg(player: Player, endPartialTurn: Boolean, isFirstPlayer: Boolean)
  final case class ClickedCardMsg(index: Int)
  final case class EndTurnMsg(firstTeamScore: Int, secondTeamScore: Int, endMatch: Boolean)
  final case class PlayedCardMsg (path: String, player: Player)
  final case class ClickedCommandMsg(command: String)
  final case class NotifyCommandMsg (command: String, player: Player)

}
