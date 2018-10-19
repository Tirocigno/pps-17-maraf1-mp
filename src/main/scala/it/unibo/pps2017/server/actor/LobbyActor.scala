
package it.unibo.pps2017.server.actor

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import it.unibo.pps2017.core.game.SimpleTeam
import it.unibo.pps2017.core.player.GameActor
import it.unibo.pps2017.discovery.restAPI.DiscoveryAPI.{IncreaseServerMatchesAPI, RegisterMatchAPI, StandardParameters}
import it.unibo.pps2017.server.controller.Dispatcher
import it.unibo.pps2017.server.model._
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.read

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


//noinspection ScalaStyle
class LobbyActor extends Actor {
  implicit val formats: DefaultFormats.type = DefaultFormats
  //implicit val akkaSystem: ActorSystem = context.system

  val mediator: ActorRef = DistributedPubSub(context.system).mediator


  val allLobby: ListBuffer[Lobby] = ListBuffer()

  override def receive: Receive = {
    /**
      * Search a game for a single player.
      */
    case SinglePlayerMsg(player, gameFoundEvent) =>
      self ! SearchPlayerMsg(allLobby.headOption, player, gameFoundEvent)

    /**
      * Search a game for a team.
      */
    case MultiPlayerMsg(player, mate, gameFoundEvent) =>
      val team: SimpleTeam = SimpleTeam("", ListBuffer(player, mate))

      self ! SearchTeamMsg(allLobby.headOption, team, gameFoundEvent)

    /**
      * Internal message for searching a game for one player.
      */
    case SearchPlayerMsg(currentLobby, player, gameFoundEvent) =>
      currentLobby match {
        case Some(lobby) =>
          try {
            lobby.addPlayer(player)
            notifyGameFound(lobby, gameFoundEvent)
          } catch {
            case _: FullLobbyException =>
              try {
                self ! SearchPlayerMsg(Some(allLobby(allLobby.indexOf(lobby) + 1)), player, gameFoundEvent)
              } catch {
                case _: Throwable => self ! SearchPlayerMsg(None, player, gameFoundEvent)
              }
            case ex: Exception => ex.printStackTrace()
          }

        case None =>
          createLobbyAndNotify(player, gameFoundEvent)
      }

    /**
      * Internal message for searching a game for team.
      */
    case SearchTeamMsg(currentLobby, team, gameFoundEvent) =>
      currentLobby match {
        case Some(lobby) =>
          try {
            lobby.addTeam(team)
            notifyGameFound(lobby, gameFoundEvent)
          } catch {
            case _: FullLobbyException =>
              try {
                self ! SearchTeamMsg(Some(allLobby(allLobby.indexOf(lobby) + 1)), team, gameFoundEvent)
              } catch {
                case _: Throwable => self ! SearchTeamMsg(None, team, gameFoundEvent)
              }
            case ex: Exception => ex.printStackTrace()
          }

        case None =>
          createLobbyAndNotify(team, gameFoundEvent)
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
    * Create the lobby for the player and fire the event param.
    *
    * @param player
    * Player id.
    * @param onGameFound
    * Event.
    */
  private def createLobbyAndNotify(player: String, onGameFound: String => Unit): Unit = {
    val lobby = createLobbyAndNotify(onGameFound)

    lobby.addPlayer(player)
    allLobby += lobby
  }


  /**
    * Create the lobby for the team and fire the event param.
    *
    * @param team
    * Team.
    * @param onGameFound
    * Event.
    */
  private def createLobbyAndNotify(team: SimpleTeam, onGameFound: String => Unit): Unit = {
    val lobby = createLobbyAndNotify(onGameFound)

    lobby.addTeam(team)
    allLobby += lobby
  }

  private def createLobbyAndNotify(onGameFound: String => Unit): Lobby = {
    val newLobby: Lobby = LobbyImpl(allLobby -= _)
    onGameFound(newLobby.id)

    newLobby
  }

  private def createActorAndNotifyTheDiscovery(game: Lobby): Unit = {
    context.system.scheduler.scheduleOnce(5 second) {
      //TODO GameActor creating
      context.system.actorOf(Props(GameActor(game.id, game.team1, game.team2, () => {
        PostRequest(Dispatcher.DISCOVERY_URL, RegisterMatchAPI.path, {
          case Some(res) => try {
            val msgFromDiscovery = read[Message](res)

            println("Discovery match registration response: " + msgFromDiscovery.message)
          } catch {
            case _: Exception => println("Unexpected message from the discovery!\nDetails: " + res)
          }
          case None =>
        }, cause => {}, Some(Map("matchID" -> game.id)), Some(Dispatcher.DISCOVERY_PORT))

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
        }, None, Some(Dispatcher.DISCOVERY_PORT))
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
