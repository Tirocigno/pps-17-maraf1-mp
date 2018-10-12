
package it.unibo.pps2017.server.model

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import it.unibo.pps2017.discovery.MatchRef

/**
  * This class is used for define the message accepted to the RouterResponse.
  *
  */

sealed trait JsonResponse

case class Game(gameId: String) extends JsonResponse

case class Message(message: String) extends JsonResponse
case class Error(cause: Option[String] = None) extends JsonResponse

case class ServerContextEncoder(ipAddress: String, port: Int) extends JsonResponse
case class MatchesSetEncoder(set: Set[MatchRef]) extends JsonResponse

object ResponseMessage {
  implicit val encodeEvent: Encoder[JsonResponse] = Encoder.instance {
    case game @ Game(_) => game.asJson
    case message @ Message(_) => message.asJson
    case error @ Error(_) => error.asJson
    case context@ServerContextEncoder(_, _) => context.asJson
    case set @ MatchesSetEncoder(_) => set.asJson
  }

  implicit val decodeEvent: Decoder[JsonResponse] =
    List[Decoder[JsonResponse]](
      Decoder[Game].widen,
      Decoder[Message].widen,
      Decoder[Error].widen,
      Decoder[ServerContextEncoder].widen,
      Decoder[MatchesSetEncoder].widen
    ).reduceLeft(_ or _)
}