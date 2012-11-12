package scajong.view

import scajong.util._
import scajong.model._

class TileClickedNotification(val tile:Tile) extends SimpleNotification
class SetupSelectedNotification(val setup:Setup) extends SimpleNotification
class HintNotification extends SimpleNotification
class MoveablesNotification extends SimpleNotification
class DoScrambleNotification extends SimpleNotification
class AddScoreNotification(val setup:Setup, val playerName:String, val ms:Int) extends SimpleNotification
class CloseViewNotification(val view:View) extends SimpleNotification

trait View extends SimplePublisher {
  def startView {}
  def stopView {}
}