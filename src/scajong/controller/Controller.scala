package scajong.controller

import scala.actors._
import scala.actors.Actor._

import scajong.util._
import scajong.model._
import scajong.view._

class Controller(val game:Game) extends SimplePublisher {
  
  private var views = List[View]()
  private var selected:Tile = null
  
  def attachView(view:View) {
    this.addSubscriber(view)
    view.startView(this)
    views = view :: views
  }
  
  def detachView(view:View) {
    view.stopView(this)
    this.remSubscriber(view)
    views = views.filter(v => v != view)
    val withoutAutoClose = views.filter(!_.autoClose)
    if (withoutAutoClose.length == 0) {
      views.foreach(_.stopView(this))
    }
    closeApplication
  }
  
  def scores = game.scores
  def tiles = game.tiles
  def setups = game.setups
  def tileTypes = game.tileTypes
  def fieldHeight = game.height
  def fieldWidth = game.width
  def canMove(tile:Tile) = game.canMove(tile)
  def calcTileIndex(tile:Tile) = game.calcTileIndex(tile)
  def setupById(id:String) = game.setupById(id)
  def topmostTile(x:Int, y:Int) = game.topmostTile(x, y)
  def hint = game.hint
  def sortedTiles = game.sortedTiles
  def findTile(x:Int, y:Int, z:Int) = game.findTile(x, y, z)
  
  def startNewGame(setup:Setup) {
    game.startNewGame(setup)
    sendNotification(new CreatedGameNotification)
    sendNotification(new StopHintNotification)
    sendNotification(new StopMoveablesNotification)
  }
  
  def scramble {
    game.scramble
    sendNotification(new ScrambledNotification)
  }
  
  def requestHint {
    val hint = game.hint
    if (hint != null) {
      game.addHintPenalty
      sendNotification(new StartHintNotification(hint))
      new Actor {
        def act {
          reactWithin(Game.HintTimeout) {
            case TIMEOUT => sendNotification(new StopHintNotification)
          }
        }
      }.start();
    }
  }
  
  def requestMoveables {
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

  def selectTile(newSelectedTile:Tile) {
    if (newSelectedTile == null) {
      if (selected != null) {
        selected = null
        sendNotification(new TileSelectedNotification(selected))
      }
    } else if (newSelectedTile == selected) {
      // Nothing to do!
    } else if (game.canMove(newSelectedTile)) {
      if (selected != null && selected.tileType == newSelectedTile.tileType) {
        val selectedTile = selected
        selected = null
        sendNotification(new TileSelectedNotification(selected))
        playTilePair(selectedTile, newSelectedTile)
      } else {
        selected = newSelectedTile
        sendNotification(new TileSelectedNotification(selected))
      }
    }
  }
  
  private def playTilePair(tile1:Tile, tile2:Tile) {
    if (game.play(tile1, tile2)) {
      sendNotification(new TilesRemovedNotification(new TilePair(tile1, tile2)))
      if (game.tiles.size == 0) {
        val time = game.gameTime
        val inScoreBoard = game.scores.isInScoreboard(game.setup, time)
        sendNotification(new WonNotification(game.setup, time, inScoreBoard))
      } else if (!game.nextMovePossible) {
        sendNotification(new NoFurtherMovesNotification)
      }
    }
  }

  def addScore(setup:Setup, playerName:String, ms:Int) {
    val position = game.scores.addScore(setup, playerName, ms)
    sendNotification(new NewScoreBoardEntryNotification(setup, position))
  }
  
  protected def closeApplication {
  	System.exit(0)
  }
}