package scajong.view.tui

import scajong.model._
import scajong.view._
import scajong.util._
import scajong.controller._

import scala.actors.Actor
import util.matching.Regex

class TextUI() extends View {

  private var run = true
  private var controller:Controller = null

  printHelp
  
  override def autoClose = true
  
  override def processNotification(sn:SimpleNotification) {
    sn match {
      case StartHintNotification(hint) => println("Hint: " + controller.calcTileIndex(hint.tile1) + " " + controller.calcTileIndex(hint.tile2))
      case StartMoveablesNotification() => printField(true)
      case CreatedGameNotification() => printField(false)
      case WonNotification(setup, ms, inScoreBoard) => println("You cleared the field in " + (ms/1000.0) + " seconds!")
      case NoFurtherMovesNotification() => println("There are no further moves! Scramble?")
      case TilesRemovedNotification(tiles) => println("Updated field:"); printField(false)
      case ScrambledNotification() => println("Scrambled field:"); printField(false)
      case _ => // Ignore all other notifications
    }
  }
  
  override def startView(controller:Controller) {
    this.controller = controller;
  }
  
  def playTiles(a:Int, b:Int) {
    if (!controller.tiles.contains(a))
      println("Could not find tile with id " + a + "!")
    else if (!controller.tiles.contains(b))
      println("Could not find tile with id " + b + "!")
    else {
      val tile1 = controller.tiles(a)
      val tile2 = controller.tiles(b)
      if (tile1 == tile2) {
        println("You have to selected two different tiles!")
      } else if (tile1.tileType.id != tile2.tileType.id) {
        println("The tiles must be of the same type!")
      } else if (!controller.canMove(tile1) || !controller.canMove(tile2)) {
        println("At least one the two selected tiles is not moveable!")
      } else {
        controller.selectTile(null)
        controller.selectTile(tile1)
        controller.selectTile(tile2)
      }
    }
  }
  
  def scramble {
    controller.scramble
  }
  
  def startGame(setupId:String) {
    val setup = controller.setupById(setupId)
    if (setup != null) {
      controller.startNewGame(setup)
    } else println("Invalid setup id!")
  }
  
  def showScores(setupId:String) {
    val setup = controller.setupById(setupId)
    if (setup != null) {
      val scores = controller.scores.getScores(setup)
      var i = 1
      for (score <- scores) {
        val time = (score.ms/1000.0)
        println("Pos: " + i + ", Seconds: " + time + ", Name: " + score.name)
        i += 1
      }
    } else println("Invalid setup id!")
  }

  def readCommand : Boolean = {
	val playRegex = new Regex("^(\\d+) (\\d+)$", "a", "b")
	val startRegex = new Regex("^start ([a-z]+?)$", "setupId")
	val scoresRegex = new Regex("^scores ([a-z]+?)$", "setupId")
	var continue = true
	readLine match {
	  case "help" => printHelp
	  case "p" => printField(false)
	  case "q" => close; continue = false
	  case "h" => printHint
	  case "m" => printMoveables
	  case "scramble" => scramble
	  case "setups" => printSetups
	  case playRegex(a, b) => playTiles(a.toInt, b.toInt)
	  case startRegex(setupId) => startGame(setupId)
	  case scoresRegex(setupId) => showScores(setupId)
	  case _ => println("Unknown command!")
	}
	continue
  }

  def printHelp {
    println("help: Show this help")
    println("q: Quit the game")
    println("p: Print game field with tiles")
    println("h: Show hint")
    println("m: Show all moveable tiles")
    println("setups: List all available setups with name and id")
    println("scramble: Scramble tiles and print field")
    println("start <setupid>: Start a new game")
    println("scores <setupid>: Show scoreboard for a setup")
    println("<Tile-Id> <Tile-Id>: Select the two tiles for removing")
    println("|-----|  Legend:")
    println("|aa bb|  aa = Tile Type")
    println("|ccccc|  bb = Height")
    println("|-----|  ccccc = Tile Id")
  }

  def splitTileIdIntoStrings(id:Int) = {
    val ifull = id.toString
    var i1:String = "  "
    var i2:String = " "
    var i3:String = "  "
      
    if (ifull.length == 1)
      i1 = ifull + " "
    else if (ifull.length == 2)
      i1 = ifull
    else if (ifull.length == 3) {
      i1 = ifull.substring(0, 2)
      i2 = ifull.substring(2, 3)
    } else if (ifull.length == 4) {
      i1 = ifull.substring(0, 2)
      i2 = ifull.substring(2, 3)
      i3 = ifull.substring(3, 4) + " "
    } else if (ifull.length >= 5) {
      i1 = ifull.substring(0, 2)
      i2 = ifull.substring(2, 3)
      i3 = ifull.substring(3, 5)
    }
    (i1,i2,i3)
  }
  
  def close {
    controller.detachView(this)
  }

  def printHint {
    controller.requestHint
  }

  def printMoveables {
    controller.requestMoveables
  }
  
  def printSetups {
    for (setup <- controller.setups) {
      println("Id: " + setup.id + ", Name: " + setup.name)
    }
  }
  
  def printField(moveablesOnly : Boolean) {
    // TODO: Split into smaller methods
    val line = "-" * (controller.fieldWidth * 3 + 2)
    println(line)
    var leftTile:Tile = null
    for (y <- 0 until controller.fieldHeight; x <- 0 until controller.fieldWidth) {
      if (x == 0) print("|")
      
      val tile = controller.topmostTile(x, y)
      if (tile == null) {
        if (leftTile == null) print("   ") else print("|  ")
      } else {
        val moveable = controller.canMove(tile)
        val id = controller.calcTileIndex(tile)
        val (i1, i2, i3) = splitTileIdIntoStrings(id)
        
        var center = " "
        if (y == tile.y || y == tile.y + Tile.Height-1)
          center = "-"
        else if (moveablesOnly && !moveable)
          center = " "
        else if (tile != null && tile.y == y-2)
          center = i2
        
        if (leftTile == tile)
          print(center)
        else
          print("|")
        
        if ((tile.x == x || tile.x + Tile.Width-1 == x) && (tile.y == y || tile.y + Tile.Height - 1 == y))
          print("--")
        else if (moveablesOnly && !moveable)
          print("  ")
        else if (tile.y == y-1 && tile.x == x-1)
          print("%2d".format(tile.z))
        else if (tile.y == y-1 && tile.x == x)
          print("%2d".format(tile.tileType.id))
        else if (tile.y == y-2 && tile.x == x)
          print(i1)
        else if (tile.y == y-2 && tile.x == x-1)
          print(i3)
        else
          print("  ")
      }
      
      if (x == controller.fieldWidth - 1) {
        print("|\n")
        leftTile = null
      } else {
        leftTile = tile
      }
    }
    println(line)
  }
}