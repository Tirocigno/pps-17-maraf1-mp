package it.unibo.pps2017.core.player

/**
  * Object that implements the command the notion of command or rather the three possible commands
  * a player can call when he has the turn.
  */

object Command {

  sealed trait Command

  case object Busso extends Command

  case object Striscio extends Command

  case object Volo extends Command


  /**
    * This method is used to get all the available commands
    *
    * @return a Iterable containing the three commands.
    */
  def values: Iterable[Command] = Iterable(Busso, Striscio, Volo)

}
