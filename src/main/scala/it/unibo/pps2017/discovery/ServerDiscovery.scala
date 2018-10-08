
package it.unibo.pps2017.discovery

/**
  * Basic trait for a server discovery implementation.
  */
trait ServerDiscovery {

  def developAPI():Unit

  def handleRestCall():Unit

}
