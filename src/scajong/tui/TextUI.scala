package scajong.tui

import scajong.model._
import util.matching.Regex

object TextUI {
  def main(args: Array[String]) {
    val game = new Game("setups/", "tiles.txt", new ReverseGenerator)
    game.startNewGame("setups/camel.txt", "Camel")
    new TextUI(game)
  }
}

class TextUI(val game:Game) {
  
  printField()
  run
  
  def playTiles(a:Int, b:Int) {
		if (!game.tiles.contains(a))
			println("Could not find tile with id " + a + "!")
		else if (!game.tiles.contains(b))
		  println("Could not find tile with id " + b + "!")
		else {
		  if (game.play(game.tiles(a), game.tiles(b))) {
		    println("Removed the two tiles!")
		    printField()
		} else
		  println("Could not remove the two tiles!")
		}
  }
  
  def run {
    while (true) {
      val command = readLine
      command match {
        case "help" => printHelp
        case "p" => printField()
        case "q" => return
        case "h" => printHint
        case "m" => printMoveables
        case "s" => game.scramble; printField()
        case s:String => {
          val regex = new Regex("^(\\d+) (\\d+)$", "a", "b")
          regex.findFirstIn(s) match {
						case Some(regex(a, b)) => playTiles(a.toInt, b.toInt)
						case None => println("Unknown command: " + s)
          }
        }
      }
    }
  }
  
  def printHelp {
    println("help: Show this help")
    println("q: Quit the game")
    println("f: Show game field with the tiles")
    println("h: Show hint")
    println("m: Show moveable tiles")
    println("s: Scramble tiles")
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
  
  def printHint {
    val hint = game.getHint
    println(game.calcTileIndex(hint.tile1) + " and " + game.calcTileIndex(hint.tile2))
  }
  
  def printMoveables {
    printField(true)
  }
  
  def printField(moveablesOnly : Boolean = false) {
    val line = "-" * (game.width * 3 + 2)
    println(line)
    var leftTile:Tile = null
    for (y <- 0 until game.height; x <- 0 until game.width) {
      if (x == 0) print("|")
      
      val tile = game.topmostTile(x, y)
      if (tile == null) {
        if (leftTile == null) print("   ") else print("|  ")
      } else {
        val moveable = game.canMove(tile)
        val id = game.calcTileIndex(tile)
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
      
      if (x == game.width - 1) {
        print("|\n")
        leftTile = null
      } else {
        leftTile = tile
      }
    }
    println(line)
  }
}