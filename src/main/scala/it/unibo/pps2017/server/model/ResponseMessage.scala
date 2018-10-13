
package it.unibo.pps2017.server.model

/**
  * This class is used for define the message accepted to the RouterResponse.
  */
sealed trait JsonResponse

case class Game(gameId: String, list: Seq[Message] = Seq(Message("ciao"), Message("giocatore1"), Message("giocatore2"))) extends JsonResponse


case class Message(message: String) extends JsonResponse
case class Error(cause: Option[String] = None) extends JsonResponse
