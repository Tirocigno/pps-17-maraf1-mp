
package it.unibo.pps2017.server.actor

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.core.player.GameActor
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI._
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.model.GameType.UNRANKED
import it.unibo.pps2017.server.model.LobbyStatusResponse.{FULL, OK, REVERSE}
import it.unibo.pps2017.server.model._
import it.unibo.pps2017.server.model.database.RedisGameUtils
import org.json4s.jackson.Serialization.read

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


class LobbyActor extends Actor {


  val allLobby: ListBuffer[Lobby] = ListBuffer()
  val mediator: ActorRef = DistributedPubSub(context.system).mediator

  override def receive: Receive = {

    /**
      * This message trigger the request of a lobby.
      */
    case TriggerSearch(team1, team2, gameFoundEvent) =>
      val firstTeam: SimpleTeam = SimpleTeam("", team1)

      val secondTeam: Option[SimpleTeam] = if (team2.nonEmpty) {
        Some(SimpleTeam("", team2))
      } else {
        None
      }

      self ! SearchLobby(allLobby.headOption, firstTeam, secondTeam, gameFoundEvent)


    /**
      * Searching a lobby for the given teams.
      *
      * Notify the caller when the game is found!
      */
    case SearchLobby(currentLobby, team1, team2, gameFoundEvent) =>
      currentLobby match {
        case Some(lobby) =>
          team2 match {
            case Some(opponents) =>
              lobby.canContains(team1, Some(opponents)) match {
                case OK =>
                  lobby.addTeam(team1)
                  lobby.addTeam(opponents)
                  notifyGameFound(lobby, gameFoundEvent)
                case REVERSE =>
                  lobby.addTeam(opponents)
                  lobby.addTeam(team1)
                  notifyGameFound(lobby, gameFoundEvent)
                case FULL =>
                  try {
                    self ! SearchLobby(Some(allLobby(allLobby.indexOf(lobby) + 1)), team1, team2, gameFoundEvent)
                  } catch {
                    case _: Throwable => self ! SearchLobby(None, team1, team2, gameFoundEvent)
                  }
              }
            case None =>
              lobby.canContains(team1, None) match {
                case OK =>
                  lobby.addTeam(team1)
                  notifyGameFound(lobby, gameFoundEvent)
                case REVERSE =>
                  lobby.addTeam(team1)
                  notifyGameFound(lobby, gameFoundEvent)
                case FULL =>
                  try {
                    self ! SearchLobby(Some(allLobby(allLobby.indexOf(lobby) + 1)), team1, team2, gameFoundEvent)
                  } catch {
                    case _: Throwable => self ! SearchLobby(None, team1, team2, gameFoundEvent)
                  }
              }
          }
        case None =>
          createLobbyAndNotify(team1, team2, gameFoundEvent)
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
  private def notifyGameFound(game: Lobby, onGameFound: String => Unit): Unit = {
    if (game.isFull) {
      allLobby -= game
      createActorAndNotifyTheDiscovery(game)
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
  private def createLobbyAndNotify(team: SimpleTeam, opponents: Option[SimpleTeam], onGameFound: String => Unit): Unit = {
    val lobby = createLobbyAndNotify(onGameFound)
    allLobby += lobby

    lobby.addTeam(team)

    opponents match {
      case Some(opponentsTeam) => lobby.addTeam(opponentsTeam)
      case None =>
    }
  }

  private def createLobbyAndNotify(onGameFound: String => Unit): Lobby = {
    val newLobby: Lobby = LobbyImpl(allLobby -= _)
    onGameFound(newLobby.id)

    newLobby
  }

  private def createActorAndNotifyTheDiscovery(game: Lobby): Unit = {
    context.system.scheduler.scheduleOnce(5 seconds) {

      RedisGameUtils().signNewGame(game.id, game.team1.asSide, game.team2.asSide, UNRANKED)
      println(s"Context -> $context / game -> $game")
      context.system.actorOf(Props(GameActor(game.id, game.team1, game.team2, () => {
        PostRequest(Dispatcher.DISCOVERY_URL, RemoveMatchAPI.path, {
          case Some(res) => try {
            val msgFromDiscovery = read[Message](res)

            println("Discovery match cancellation response: " + msgFromDiscovery.message)
          } catch {
            case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
          }
          case None =>
        }, cause => {}, Some(Map("matchID" -> game.id)), Some(Dispatcher.DISCOVERY_PORT))

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
        }, None, Some(Dispatcher.DISCOVERY_PORT))


        RedisGameUtils().setGameEnd(game.id, UNRANKED)
      })))


      PostRequest(Dispatcher.DISCOVERY_URL, RegisterMatchAPI.path, {
        case Some(res) => try {
          val msgFromDiscovery = read[Message](res)

          println("Discovery match registration response: " + msgFromDiscovery.message)
        } catch {
          case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
        }
        case None =>
      }, cause => {}, Some(Map("matchID" -> game.id, StandardParameters.IP_KEY -> Dispatcher.MY_IP, StandardParameters.PORT_KEY -> "4700")), Some(Dispatcher.DISCOVERY_PORT))


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
      }, Some(Map(StandardParameters.IP_KEY -> Dispatcher.MY_IP, StandardParameters.PORT_KEY -> "4700")), Some(Dispatcher.DISCOVERY_PORT))
    }
  }
}
