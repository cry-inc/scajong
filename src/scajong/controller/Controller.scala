package scajong.controller

import scajong.util._
import scajong.model._
import scajong.view._

class Controller(val game:Game) extends SimpleSubscriber {
  
  var views = List[View]()
  
  override def processNotifications(sn:SimpleNotification) {
    sn match {
      case n: TileClickedNotification => tileClicked(n.tile)
      case n: SetupSelectedNotification => game.startNewGame(n.setup)
      case n: HintNotification => game.addPenalty(15000)
      case n: MoveablesNotification => game.addPenalty(5000)
      case n: AddScoreNotification => addScore(n.setup, n.playerName, n.ms)
      case n: CloseViewNotification => detachView(n.view)
      case n: DoScrambleNotification => game.scramble
      case _ => // Nothing
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
      if (game.selected != null && game.selected.tileType == tile.tileType) {
        game.play(game.selected, tile)
        game.selected = null
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