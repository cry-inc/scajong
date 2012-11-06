package scajong.controller

import scajong.model._
import scajong.view._
import swing._
import swing.event._

class SwingController(val field:Field) extends Reactor {
  
  var views:List[SwingView] = Nil
  
  reactions += {
    case e: TileClickedEvent => tileClicked(e.tile)
    case e: ShowScoresEvent => println("show scores")
    case e: HintEvent => println("show hint")
    case e: MoveablesEvent => println("show moveables")
    case e: SetupSelectedEvent => field.startNewGame(e.setupFile, e.setupName)
  }
  
  def attachView(view:SwingView) {
    views = view :: views
    listenTo(view)
  }
  
  def detachView(view:SwingView) {
    views = views.filter(v => v != view)
    deafTo(view)
  }
  
  def tileClicked(tile:Tile) {
    println("tileClicked: " + tile)
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