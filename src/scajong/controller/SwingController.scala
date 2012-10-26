package scajong.controller

import scajong.model._
import scajong.view._
import swing._

class SwingController(val field:Field) extends Reactor {
  
  var views:List[SwingView] = Nil
  
  reactions += {
    case e: TileClickedEvent => tileClicked(e.tile)
  }
  
  def attachView(view:SwingView) {
    views = view :: views
    listenTo(view.fieldPanel)
  }
  
  def detachView(view:SwingView) {
    views = views.filter(v => v != view)
    deafTo(view.fieldPanel)
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