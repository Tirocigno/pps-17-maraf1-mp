
package it.unibo.pps2017.server.model

/**
  * This class is used for define the message accepted to the RouterResponse.
  */
sealed trait JsonResponse

case class GameFound(gameId: String) extends JsonResponse


case class Message(message: String) extends JsonResponse

case class Error(cause: Option[String] = None) extends JsonResponse

case class GameHistory(gameId: String, teams: Seq[Side], gameSet: GameSet) extends JsonResponse

case class GameSet(player1Hand: Seq[String],
                   player2Hand: Seq[String],
                   player3Hand: Seq[String],
                   player4Hand: Seq[String],
                   commands: Seq[String]) extends JsonResponse

case class Side(members: Seq[String]) extends JsonResponse