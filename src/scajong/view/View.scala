package scajong.view

import scajong.util._
import scajong.model._

case class TileClickedNotification(val tile:Tile) extends SimpleNotification
case class SetupSelectedNotification(val setup:Setup) extends SimpleNotification
case class HintNotification() extends SimpleNotification
case class MoveablesNotification() extends SimpleNotification
case class DoScrambleNotification() extends SimpleNotification
case class AddScoreNotification(val setup:Setup, val playerName:String, val ms:Int) extends SimpleNotification
case class CloseViewNotification(val view:View) extends SimpleNotification

trait View extends SimplePublisher {
  def startView(game:Game) {}
  def stopView(game:Game) {}
  def autoClose = false
}