

package it.unibo.pps2017.server.model

import it.unibo.pps2017.commons.remote.rest.RestUtils.MatchRef
import it.unibo.pps2017.discovery.structures.SocialActorsMap.SocialMap


/**
  * This class is used for define the message accepted to the RouterResponse.
  *
  */

sealed trait JsonResponse

case class Message(message: String) extends JsonResponse
case class Error(cause: Option[String] = None) extends JsonResponse

case class GameFound(gameId: String) extends JsonResponse
case class ServerContextEncoder(ipAddress: String, port: Int) extends JsonResponse
case class MatchesSetEncoder(set: Set[MatchRef]) extends JsonResponse
case class GameHistory(gameId: String, teams: Seq[Side], gameSet: GameSet) extends JsonResponse
case class GameSet(player1Hand: Seq[String],
                   player2Hand: Seq[String],
                   player3Hand: Seq[String],
                   player4Hand: Seq[String],
                   commands: Seq[String]) extends JsonResponse
case class Side(members: Seq[String]) extends JsonResponse
case class User(username: String, score: Int) extends JsonResponse
case class UserFriends(username: String, friends: Seq[String]) extends JsonResponse


case class OnlinePlayersMapEncoder(map: SocialMap) extends JsonResponse

