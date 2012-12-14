package scajong.controller

import scajong.util._
import scajong.model._
import scajong.view._
import scajong.view.AddScoreNotification

class Controller(val game:Game) extends SimpleSubscriber {
  
  var views = List[View]()
  
  override def processNotification(sn:SimpleNotification) {
    sn match {
      case TileClickedNotification(tile) => tileClicked(tile)
      case SetupSelectedNotification(setup) => game.startNewGame(setup)
      case HintNotification() => game.addPenalty(15000)
      case MoveablesNotification() => game.addPenalty(5000)
      case AddScoreNotification(setup, playerName, ms) => addScore(setup, playerName, ms)
      case CloseViewNotification(view) => detachView(view)
      case DoScrambleNotification() => game.scramble
    }
  }

  def attachView(view:View) {
    view.addSubscriber(this)
    view.startView(game)
    views = view :: views
  }

  def detachView(view:View) {
    view.stopView(game)
    view.remSubscriber(this)
    views = views.filter(v => v != view)
    val withoutAutoClose = views.filter(!_.autoClose)
    if (withoutAutoClose.length == 0) closeApplication
  }

  private def tileClicked(tile:Tile) {
    if (game.canMove(tile)) {
      val tmpSelected = game.selected
      if (tmpSelected != null && tmpSelected.tileType == tile.tileType) {
        game.selected = null
        game.play(tmpSelected, tile)
      } else {
        game.selected = tile
      }
    }
  }

  private def addScore(setup:Setup, playerName:String, ms:Int) {
    game.scores.addScore(setup, playerName, ms)
  }
  
  def closeApplication {
  	System.exit(0)
  }
}