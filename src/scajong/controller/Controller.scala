package scajong.controller

import scala.actors._
import scala.actors.Actor._

import scajong.util._
import scajong.model._
import scajong.view._

class Controller(val game:Game) extends SimpleSubscriber with SimplePublisher {
  
  var views = List[View]()
  
  override def processNotification(sn:SimpleNotification) {
    sn match {
      case TileClickedNotification(tile) => tileClicked(tile)
      case SetupSelectedNotification(setup) => startNewGame(setup)
      case RequestHintNotification() => hint; println("request")
      case RequestMoveablesNotification() => moveables
      case AddScoreNotification(setup, playerName, ms) => addScore(setup, playerName, ms)
      case CloseViewNotification(view) => detachView(view)
      case DoScrambleNotification() => game.scramble
    }
  }

  def attachView(view:View) {
    view.addSubscriber(this)
    this.addSubscriber(view)
    view.startView(game)
    views = view :: views
  }

  def detachView(view:View) {
    view.stopView(game)
    this.remSubscriber(view)
    view.remSubscriber(this)
    views = views.filter(v => v != view)
    val withoutAutoClose = views.filter(!_.autoClose)
    if (withoutAutoClose.length == 0) closeApplication
  }
  
  private def startNewGame(setup:Setup) {
    sendNotification(new StopHintNotification)
    sendNotification(new StopMoveablesNotification)
    game.startNewGame(setup)
  }
  
  private def hint {
    val (hint, timeout) = game.requestHint
    sendNotification(new StartHintNotification(hint))
    new Actor {
      def act {
        reactWithin(timeout) {
          case TIMEOUT => sendNotification(new StopHintNotification)
        }
      }
    }.start();
  }
  
  private def moveables {
    val timeout = game.requestMoveables
    sendNotification(new StartMoveablesNotification)
    new Actor {
      def act {
        reactWithin(timeout) {
          case TIMEOUT => sendNotification(new StopMoveablesNotification)
        }
      }
    }.start();
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