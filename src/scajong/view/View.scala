package scajong.view

import scajong.util._
import scajong.model._

class TileClickedNotification(val tile:Tile) extends SimpleNotification
class SetupSelectedNotification(val setupFile:String, val setupName:String) extends SimpleNotification
class HintNotification extends SimpleNotification
class MoveablesNotification extends SimpleNotification

trait View extends SimplePublisher