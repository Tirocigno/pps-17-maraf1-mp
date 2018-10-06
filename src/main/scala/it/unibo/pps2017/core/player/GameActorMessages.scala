package it.unibo.pps2017.core.player

//#messages
final case class ReadyToPlay(text: String)
final case class DistributedCard(cards: List[String])
final case class ClickedCard(index: Int)
final case class ClickedCommand(command: String)
//#messages


