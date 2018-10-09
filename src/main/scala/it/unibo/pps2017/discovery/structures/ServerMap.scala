
package it.unibo.pps2017.discovery.structures

import it.unibo.pps2017.discovery.ServerContext

/**
  * A structure to register and check status of all servers in the system.
  */
trait ServerMap {

  /**
    * Add a new server
    *
    * @param server the IP address and Port of the server.
    */
  def addServer(server: ServerContext): Unit

  /**
    * Remove a server from the map.
    *
    * @param server the IP address and Port of the server to remove.
    */
  def removeServer(server: ServerContext):Unit

  /**
    * Check what server has the less amount of matches and returns its IP.
    *
    * @return less busy server IP Address and Port.
    */
  def getLessBusyServer: ServerContext

  /**
    * Increase the number of matches played in the server at the specified IP
    *
    * @param server the IP address and Port of the server which register a new match.
    */
  def increaseMatchesPlayedOnServer(server: ServerContext):Unit

  /**
    * Decrease the number of matches played in the server at the specified IP
    *
    * @param server the IP address of the server which unregister a new match.
    */
  def decreaseMatchesPlayedOnServer(server: ServerContext): Unit
}

object ServerMap {

  def apply(): ServerMap = new ServerMapImpl()

  private class ServerMapImpl extends ServerMap {
    var matchesMap: scala.collection.mutable.Map[ServerContext, Int] = scala.collection.mutable.Map[ServerContext, Int]()

    override def addServer(serverContext: ServerContext): Unit = matchesMap += (serverContext -> 0)

    override def removeServer(server: ServerContext): Unit = matchesMap -= server

    override def getLessBusyServer: ServerContext = matchesMap.toSeq.sortBy(_._2).map(_._1).head

    override def increaseMatchesPlayedOnServer(server: ServerContext): Unit = {
      var matchesPlayed = matchesMap.getOrElse(server, throw new IllegalArgumentException)
      matchesPlayed = matchesPlayed + 1
    }


    override def decreaseMatchesPlayedOnServer(server: ServerContext): Unit = {
      var matchesPlayed = matchesMap.getOrElse(server, throw new IllegalArgumentException)
      matchesPlayed = matchesPlayed - 1
    }
  }
}

