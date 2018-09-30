package it.unibo.pps2017.core.player


/**
  * This trait define the concept of player, who has a username.
  */


sealed trait Player {

  def userName: String

  override def equals(obj: Any): Boolean = obj match {
    case PlayerImpl(username) if userName.equals(username) => true
    case _ => false
  }

  override def hashCode(): Int = super.hashCode()
}

/**
  * Basic implementation of player.
  *
  * @param userName  the username of the player.
  */
case class PlayerImpl(override val userName: String) extends Player

