
package it.unibo.pps2017.server.model

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

/**
  * This class is used for define the message accepted to the RouterResponse.
  *
  */

trait JsonResponse

case class Game(gameId: String) extends JsonResponse

case class Message(message: String) extends JsonResponse
case class Error(cause: Option[String] = None) extends JsonResponse

object ResponseMessage {
  implicit val encodeEvent: Encoder[JsonResponse] = Encoder.instance {
    case game @ Game(_) => game.asJson
    case message @ Message(_) => message.asJson
    case error @ Error(_) => error.asJson
  }

  implicit val decodeEvent: Decoder[JsonResponse] =
    List[Decoder[JsonResponse]](
      Decoder[Game].widen,
      Decoder[Message].widen,
      Decoder[Error].widen
    ).reduceLeft(_ or _)
}