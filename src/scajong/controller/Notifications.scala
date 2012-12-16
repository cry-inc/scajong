package scajong.controller

import scajong.model._
import scajong.util.SimpleNotification

// Notifications for the views from the controller
case class StartHintNotification(hint:TilePair) extends SimpleNotification
case class StopHintNotification() extends SimpleNotification
case class StartMoveablesNotification() extends SimpleNotification
case class StopMoveablesNotification() extends SimpleNotification
case class WonNotification(val setup:Setup, val ms:Int, val inScoreBoard:Boolean) extends SimpleNotification
case class NoFurtherMovesNotification() extends SimpleNotification
case class TilesRemovedNotification(val tiles:TilePair) extends SimpleNotification
case class TileSelectedNotification(val tile:Tile) extends SimpleNotification
case class ScrambledNotification() extends SimpleNotification
case class CreatedGameNotification() extends SimpleNotification
case class NewScoreBoardEntryNotification(val setup:Setup, val position:Int) extends SimpleNotification