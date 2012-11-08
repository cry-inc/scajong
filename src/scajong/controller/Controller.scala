package scajong.controller

import scajong.util._
import scajong.model._
import scajong.view._

class SwingController(val game:Game) extends SimpleSubscriber {
  
  override def processNotifications(sn:SimpleNotification) {
    sn match {
      case n: TileClickedNotification => tileClicked(n.tile)
      case n: SetupSelectedNotification => game.startNewGame(n.setupFile, n.setupName)
      case n: HintNotification => // TODO: add hint penalty to model
      case n: MoveablesNotification => // TODO: add moveables penalty to model
      case n: AddScoreNotification => addScore(n.setup, n.playerName, n.ms)
      case n: CloseViewNotification => detachView(n.view)
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
  
  def addScore(setup:String, playerName:String, ms:Int) {
    game.scores.addScore(setup, playerName, ms)
  }
}