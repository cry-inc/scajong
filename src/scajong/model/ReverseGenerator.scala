package scajong.model

import scajong.util.FileUtil
import scala.io.Source
import scala.util.Random
import util.matching.Regex

class ReverseGenerator extends Generator {
  
  def scramble(game:Game) {
    var reversed = List[TilePair]()
    val random = new Random()
    
    while (game.tiles.size > 0) {
      // Find two or more random outer tiles, remove them and store the coordinates
      var removables = extractRemovableTiles(game)
      // Continue until no more random removable tile pairs are left in the list
      while (removables.length > 1) {
        val index1 = random.nextInt(removables.length)
        var index2 = random.nextInt(removables.length)
        while (index1 == index2) index2 = (index2 + 1) % removables.length
        val pair = new TilePair(removables(index1), removables(index2))
        reversed = pair :: reversed
        removables = removables.filter(t => t != pair.tile1 && t != pair.tile2)
      }
      // Add remaining tile to the field
      removables.foreach(game += _)
    }    
    
    // Read the list from behind and and get random a random tile type for each pair to build the game
    var typeRand = new Random().nextInt(game.tileTypes.length)
    for (pair <- reversed) {
      val typeIndex = typeRand % game.tileTypes.length
      typeRand += 1
      pair.tile1.tileType = game.tileTypes(typeIndex)
      pair.tile2.tileType = game.tileTypes(typeIndex)
      game += pair.tile1;
      game += pair.tile2;
    }
  }
  
  def generate(game:Game, setupFile:String) {
    // Place the full set without a tile type
    loadStructure(game, null, setupFile);
    // Set the tile types in a solvable order
    scramble(game);
  }
  
  def loadStructure(game:Game, tileType:TileType, setupFile:String) {
    game.tiles = Map()
    val lines = FileUtil.readLines(setupFile)
    val regexTile = new Regex("^(\\d+) (\\d+) (\\d+)$", "x", "y", "z")
    val regexDims = new Regex("^(\\d+) (\\d+)$", "width", "height")
    lines.foreach({ _ match {
	    case regexTile(x, y, z) => game += new Tile(x.toInt, y.toInt, z.toInt, tileType)
	    case regexDims(width, height) => game.width = width.toInt; game.height = height.toInt
	    case _ => // Nothing
    }})
  }
  
  def extractRemovableTiles(game:Game) : List[Tile] = {
    val removables = game.tiles.filter(p => game.canMove(p._2))
    removables.foreach(game.tiles -= _._1)
    removables.map(_._2).toList
  }
}