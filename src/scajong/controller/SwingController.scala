package scajong.controller

import scajong.util._
import scajong.model._
import scajong.view._

class SwingController(val field:Field) extends SimpleSubscriber {
  
  override def processNotifications(sn:SimpleNotification) {
    sn match {
      case n: TileClickedNotification => tileClicked(n.tile)
      case n: SetupSelectedNotification => field.startNewGame(n.path, n.name)
      case n: HintNotification => // TODO: add hint penalty to model
      case n: MoveablesNotification => // TODO: add moveables penalty to model
      case _ => // Nothing
    }
  }

  def attachView(view:SwingView) {
    view.addSubscriber(this)
  }
  
  def detachView(view:SwingView) {
    view.remSubscriber(this)
  }
  
  def tileClicked(tile:Tile) {
    if (field.canMove(tile)) {
      if (field.selected != null && field.selected.tileType == tile.tileType) {
        field.play(field.selected, tile)
        field.selected = null
      } else {
        field.selected = tile
      }
    }
  }
}