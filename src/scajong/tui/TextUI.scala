package scajong.tui

import scajong.model._
import util.matching.Regex

object TextUI {
  def main(args: Array[String]) {
    val field = new Field(new ReverseGenerator("setup.txt", "tiles.txt"))
    new TextUI(field)
  }
}

class TextUI(val field:Field) {
  
  printField
  run
  
  def playTiles(a:Int, b:Int) {
		if (!field.tiles.contains(a))
			println("Could not find tile with id " + a + "!")
		else if (!field.tiles.contains(b))
		  println("Could not find tile with id " + b + "!")
		else {
		  if (field.play(field.tiles(a), field.tiles(b))) {
		    println("Removed the two tiles!")
		    printField
		} else
		  println("Could not remove the two tiles!")
		}
  }
  
  def run {
    while (true) {
      val command = readLine
      command match {
        case "h" => printHelp
        case "p" => printField
        case "q" => return
        case "s" => field.scramble; printField
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
    println("h: Show this help.")
    println("q: Quit game.")
    println("p: Print the game field on the console.")
    println("s: Scramble tiles")
    println("<Tile-Id> <Tile-Id>: Select the two tiles for removing.")
    println("|-----|  Legend:")
	  println("|aa bb|  aa = Tile Type")
	  println("|ccccc|  bb = Height")
	  println("|-----|  ccccc = Tile Id")
  }
  
  def printField {
    val line = "-" * (field.width * 3 + 2)
    println(line)
    var leftTile:Tile = null
    for (y <- 0 until field.height; x <- 0 until field.width) {
      if (x == 0) print("|")
      val tile = field.topmostTile(x, y)
      
      if (tile == null) {
        if (leftTile == null) print("   ") else print("|  ")
      } else {
        val i = field.calcTileIndex(tile)
        val ifull = i.toString
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
        } else if (ifull.length == 5) {
          i1 = ifull.substring(0, 2)
          i2 = ifull.substring(2, 3)
          i3 = ifull.substring(3, 5)
        }
        
        var center = " "
        if (y == tile.y || y == tile.y + Tile.Height-1)
          center = "-"
        else if (tile != null && tile.y == y-2)
          center = i2
        
        if (leftTile == tile)
          print(center)
        else
          print("|")

	    	if ((tile.x == x || tile.x + Tile.Width-1 == x) && (tile.y == y || tile.y + Tile.Height - 1 == y))
	    	  print("--")
	    	else if (tile.y == y-1 && tile.x == x-1)
	    	  print("%2d".format(tile.z))
	    	else if (tile.y == y-1 && tile.x == x)
	    	  print("%2d".format(tile.tileType.id))
	    	else if (tile.y == y-2 && tile.x == x)
	    	  print(i1)
	    	else if (tile.y == y-2 && tile.x == x-1)
	    	  printf(i3)
	    	else
	    	  printf("  ")
      }
      
      if (x == field.width - 1) {
        print("|\n")
        leftTile = null
      } else {
        leftTile = tile
      }
    }
    println(line)
  }
}