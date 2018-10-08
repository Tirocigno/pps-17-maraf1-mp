
package it.unibo.pps2017.discovery

/**
  * A structure to register and check status of all servers in the system.
  */
trait ServerMap {

  /**
    * Add a new server
    * @param ipAddress the IP address of the server.
    */
  def addServer(ipAddress: IPAddress):Unit

  /**
    * Remove a server from the map.
    * @param ipAddress the IP address of the server to remove.
    */
  def removeServer(ipAddress: IPAddress):Unit

  /**
    * Check what server has the less amount of matches and returns its IP.
    * @return less busy server IP Address.
    */
  def getLessBusyServer:IPAddress

  /**
    * Increase the number of matches played in the server at the specified IP
    * @param ipAddress the IP address of the server which register a new match.
    */
  def increaseMatchesPlayedOnServer(ipAddress: IPAddress):Unit

  /**
    * Decrease the number of matches played in the server at the specified IP
    * @param ipAddress the IP address of the server which unregister a new match.
    */
  def decreaseMatchesPlayedOnServer(ipAddress: IPAddress):Unit

  /**
    * Check all server status,
    * The discovery call a check API on each server, if a server is unreachable, is deleted from the list.
    */
  def checkAllServersStatus():Unit
}

