package scajong.model

import scajong.util.SimpleNotification

// Notifications towards the views
case class StartHintNotification(hint:TilePair) extends SimpleNotification
case class StopHintNotification() extends SimpleNotification
case class StartMoveablesNotification() extends SimpleNotification
case class StopMoveablesNotification() extends SimpleNotification

case class WonNotification(val setup:Setup, val ms:Int, val inScoreBoard:Boolean) extends SimpleNotification
case class NoFurtherMovesNotification() extends SimpleNotification
case class TileRemovedNotification(val tile:Tile) extends SimpleNotification
case class ScrambledNotification() extends SimpleNotification
case class SelectedTileNotification(val tile:Tile) extends SimpleNotification
case class CreatedGameNotification() extends SimpleNotification
case class NewScoreBoardEntryNotification(val setup:Setup, val position:Int) extends SimpleNotification