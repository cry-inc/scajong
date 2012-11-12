package scajong.controller

import scajong.util._
import scajong.model._
import scajong.view._

class SwingController(val game:Game) extends SimpleSubscriber {
  
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
    view.startView
  }
  
  def detachView(view:View) {
    view.stopView
    view.remSubscriber(this)
  }
  
  def tileClicked(tile:Tile) {
    if (game.canMove(tile)) {
      if (game.selected != null && game.selected.tileType == tile.tileType) {
        game.play(game.selected, tile)
        game.selected = null
      } else {
        game.selected = tile
      }
    }
  }
  
  def addScore(setup:Setup, playerName:String, ms:Int) {
    game.scores.addScore(setup, playerName, ms)
  }
}