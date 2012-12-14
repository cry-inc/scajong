package scajong.controller

import scala.actors._
import scala.actors.Actor._

import scajong.util._
import scajong.model._
import scajong.view._

class Controller(val game:Game) extends SimpleSubscriber with SimplePublisher {
  
  private var views = List[View]()
  private var selected:Tile = null
  
  override def processNotification(sn:SimpleNotification) {
    sn match {
      case TileClickedNotification(tile) => tileClicked(tile)
      case SetupSelectedNotification(setup) => startNewGame(setup)
      case RequestHintNotification() => hint
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
    game.startNewGame(setup)
    sendNotification(new CreatedGameNotification)
    sendNotification(new StopHintNotification)
    sendNotification(new StopMoveablesNotification)
  }
  
  private def scramble {
    game.scramble
    sendNotification(new ScrambledNotification)
  }
  
  private def hint {
    game.addHintPenalty
    sendNotification(new StartHintNotification(game.hint))
    new Actor {
      def act {
        reactWithin(Game.HintTimeout) {
          case TIMEOUT => sendNotification(new StopHintNotification)
        }
      }
    }.start();
  }
  
  private def moveables {
    game.addMoveablesPenalty
    sendNotification(new StartMoveablesNotification)
    new Actor {
      def act {
        reactWithin(Game.MoveablesTimeout) {
          case TIMEOUT => sendNotification(new StopMoveablesNotification)
        }
      }
    }.start();
  }

  private def tileClicked(clickedTile:Tile) {
    if (game.canMove(clickedTile)) {
      if (selected != null && selected.tileType == clickedTile.tileType) {
        val selectedTile = selected
        selected = null
        sendNotification(new TileSelectedNotification(null))
        playTilePair(selectedTile, clickedTile)
      } else {
        selected = clickedTile
        sendNotification(new TileSelectedNotification(selected))
      }
    }
  }
  
  private def playTilePair(tile1:Tile, tile2:Tile) {
    if (game.play(tile1, tile2)) {
      sendNotification(new TileRemovedNotification(tile1))
      sendNotification(new TileRemovedNotification(tile2))
      if (game.tiles.size == 0) {
        val time = game.gameTime
        val inScoreBoard = game.scores.isInScoreboard(game.setup, time)
        sendNotification(new WonNotification(game.setup, time, inScoreBoard))
      } else if (!game.nextMovePossible) {
        sendNotification(new NoFurtherMovesNotification)
      }
    }
  }

  private def addScore(setup:Setup, playerName:String, ms:Int) {
    val position = game.scores.addScore(setup, playerName, ms)
    sendNotification(new NewScoreBoardEntryNotification(setup, position))
  }
  
  def closeApplication {
  	System.exit(0)
  }
}