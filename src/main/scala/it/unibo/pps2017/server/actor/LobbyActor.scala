
package it.unibo.pps2017.server.actor

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.core.player.GameActor
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{StandardParameters, _}
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.model.GameType.{GameType, RANKED, UNRANKED}
import it.unibo.pps2017.server.model.LobbyStatusResponse.{FULL, OK, REVERSE}
import it.unibo.pps2017.server.model._
import it.unibo.pps2017.server.model.database.{RedisGameUtils, RedisUserUtils}
import org.json4s.jackson.Serialization.read

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


class LobbyActor extends Actor {


  val unrankedLobbys: ListBuffer[Lobby] = ListBuffer()
  val rankedLobbys: ListBuffer[Lobby] = ListBuffer()
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {

    /**
      * This message trigger the request of a lobby.
      */
    case TriggerSearch(team1, team2, gameFoundEvent, gameType) =>
      val firstTeam: SimpleTeam = SimpleTeam("", team1)

      val secondTeam: Option[SimpleTeam] = if (team2.nonEmpty) {
        Some(SimpleTeam("", team2))
      } else {
        None
      }


      gameType match {
        case UNRANKED =>
          self ! SearchLobby(unrankedLobbys.headOption, firstTeam, secondTeam, gameFoundEvent, gameType)
        case RANKED =>
          self ! SearchLobby(rankedLobbys.headOption, firstTeam, secondTeam, gameFoundEvent, gameType)
      }


    /**
      * Searching a lobby for the given teams.
      *
      * Notify the caller when the game is found!
      */
    case SearchLobby(currentLobby, team1, team2, gameFoundEvent, gameType) =>
      currentLobby match {
        case Some(lobby) =>
          team2 match {
            case Some(opponents) =>
              lobby.canContains(team1, Some(opponents)) match {
                case OK =>
                  lobby.addTeam(team1, Lobby.PARTNER_ADD)
                  lobby.addTeam(opponents, Lobby.FOE_ADD)
                  notifyGameFound(lobby, gameFoundEvent, gameType)
                case REVERSE =>
                  lobby.addTeam(opponents, Lobby.PARTNER_ADD)
                  lobby.addTeam(team1, Lobby.FOE_ADD)
                  notifyGameFound(lobby, gameFoundEvent, gameType)
                case FULL =>
                  nextLobby(lobby, team1, team2, gameFoundEvent, gameType)
              }
            case None =>
              lobby.canContains(team1, None) match {
                case OK =>
                  lobby.addTeam(team1, Lobby.CASUAL_ADD)
                  notifyGameFound(lobby, gameFoundEvent, gameType)
                case REVERSE =>
                  lobby.addTeam(team1, Lobby.CASUAL_ADD)
                  notifyGameFound(lobby, gameFoundEvent, gameType)
                case FULL =>
                  nextLobby(lobby, team1, team2, gameFoundEvent, gameType)
              }
          }
        case None =>
          createLobbyAndNotify(team1, team2, gameFoundEvent, gameType)
      }


  }


  private def nextLobby(lobby: Lobby,
                        team1: SimpleTeam, team2: Option[SimpleTeam],
                        onGameFound: String => Unit, gameType: GameType): Unit = {
    try {
      gameType match {
        case UNRANKED =>
          self ! SearchLobby(Some(unrankedLobbys(unrankedLobbys.indexOf(lobby) + 1)), team1, team2, onGameFound, gameType)
        case RANKED =>
          self ! SearchLobby(Some(rankedLobbys(rankedLobbys.indexOf(lobby) + 1)), team1, team2, onGameFound, gameType)
      }
    } catch {
      case _: Throwable => self ! SearchLobby(None, team1, team2, onGameFound, gameType)
    }
  }

  /**
    * Notify when the lobby is found.
    * Remove the lobby if is full.
    *
    * @param game
    * Lobby.
    * @param onGameFound
    * Event to fire.
    */
  private def notifyGameFound(game: Lobby, onGameFound: String => Unit, gameType: GameType): Unit = {
    if (game.isFull) {
      gameType match {
        case UNRANKED => unrankedLobbys -= game
        case RANKED => rankedLobbys -= game
      }
      createActorAndNotifyTheDiscovery(game, gameType)
    }
    onGameFound(game.id)
  }

  /**
    * Create the lobby for  and fire the event param.
    *
    * @param team
    * Team.
    * @param onGameFound
    * Event.
    */
  private def createLobbyAndNotify(team: SimpleTeam, opponents: Option[SimpleTeam], onGameFound: String => Unit, gameType: GameType): Unit = {
    def createLobby(onGameFound: String => Unit, gameType: GameType): Lobby = {
      val newLobby: Lobby = gameType match {
        case UNRANKED =>
          LobbyImpl(unrankedLobbys -= _)
        case RANKED =>
          LobbyImpl(rankedLobbys -= _)
      }

      gameType match {
        case UNRANKED => unrankedLobbys += newLobby
        case RANKED => rankedLobbys += newLobby
      }

      onGameFound(newLobby.id)

      newLobby
    }


    val lobby = createLobby(onGameFound, gameType)
    lobby.addTeam(team, Lobby.PARTNER_ADD)

    opponents match {
      case Some(opponentsTeam) => lobby.addTeam(opponentsTeam, Lobby.FOE_ADD)
      case None =>
    }


  }


  private def createActorAndNotifyTheDiscovery(game: Lobby, gameType: GameType): Unit = {
    context.system.scheduler.scheduleOnce(5 seconds) {

      RedisGameUtils().signNewGame(game.id, game.team1.asSide, game.team2.asSide, gameType)
      println(s"Context -> $context / game -> $game")
      context.system.actorOf(Props(GameActor(game.id, game.team1, game.team2, (winners: Side) => {
        PostRequest(Dispatcher.DISCOVERY_URL, RemoveMatchAPI.path, {
          case Some(res) => try {
            val msgFromDiscovery = read[Message](res)

            println("Discovery match cancellation response: " + msgFromDiscovery.message)
          } catch {
            case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
          }
          case None =>
        }, _ => {}, Some(Map("matchID" -> game.id,
          StandardParameters.PORT_KEY -> Dispatcher.PORT)), Some(Dispatcher.DISCOVERY_PORT))

        PostRequest(Dispatcher.DISCOVERY_URL, DecreaseServerMatchesAPI.path, {
          case Some(res) => try {
            val msgFromDiscovery = read[Message](res)

            println("Discovery match cancellation response: " + msgFromDiscovery.message)
          } catch {
            case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
          }
          case None =>
        }, cause => {
          println("Error on connection with the Discovery!\nDetails: " + cause.getMessage)
        }, Some(Map(StandardParameters.PORT_KEY -> Dispatcher.PORT)), Some(Dispatcher.DISCOVERY_PORT))


        if (gameType == RANKED) {
          winners.members foreach {
            RedisUserUtils().incrementScore(_, result => {
              println("Score increment result -> " + result)
            }, cause => {
              println(s"Error on score increment. \nDetails: ${cause.getMessage}")
            })
          }
          if (equalsSide(winners, game.team1.asSide)) {
            game.team2.getMembers foreach {
              RedisUserUtils().decrementScore(_, result => {
                println("Score increment result -> " + result)
              }, cause => {
                println(s"Error on score increment. \nDetails: ${cause.getMessage}")
              })
            }
          } else {
            game.team1.getMembers foreach {
              RedisUserUtils().incrementScore(_, result => {
                println("Score increment result -> " + result)
              }, cause => {
                println(s"Error on score increment. \nDetails: ${cause.getMessage}")
              })
            }
          }


        }

        RedisGameUtils().setGameEnd(game.id, gameType)
      })))


      PostRequest(Dispatcher.DISCOVERY_URL, RegisterMatchAPI.path, {
        case Some(res) => try {
          val msgFromDiscovery = read[Message](res)

          println("Discovery match registration response: " + msgFromDiscovery.message)
        } catch {
          case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
        }
        case None =>
      }, _ => {}, Some(Map("matchID" -> game.id,
        StandardParameters.PORT_KEY -> "4700")), Some(Dispatcher.DISCOVERY_PORT))


      PostRequest(Dispatcher.DISCOVERY_URL, IncreaseServerMatchesAPI.path, {
        case Some(res) => try {
          val msgFromDiscovery = read[Message](res)

          println("Discovery match registration response: " + msgFromDiscovery.message)
        } catch {
          case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
        }
        case None =>
      }, cause => {
        println("Error on connection with the Discovery!\nDetails: " + cause.getMessage)
      }, Some(Map(StandardParameters.PORT_KEY -> Dispatcher.PORT)), Some(Dispatcher.DISCOVERY_PORT))
    }
  }


  private def equalsSide(side1: Side, side2: Side): Boolean = side1.members.sorted == side2.members.sorted
}






