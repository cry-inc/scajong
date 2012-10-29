package scajong.model

import scala.io.Source
import scala.util.Random
import util.matching.Regex

class ReverseGenerator(val setupFile:String, val tileFile:String) extends IGenerator {
  
  def scramble(field:Field) {
    var reversed = List[TilePair]()
    val random = new Random()
    
    while (field.tiles.size > 0) {
      // Find two or more random outer tiles, remove them and store the coordinates
      var removables = extractRemovableTiles(field)
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
      removables.foreach(field += _)
    }    
    
    // Read the list from behind and and get random a random tile type for each pair to build the game
    var typeRand = new Random().nextInt(field.tileTypes.length)
    for (pair <- reversed) {
      val typeIndex = typeRand % field.tileTypes.length
      typeRand += 1
      pair.tile1.tileType = field.tileTypes(typeIndex)
      pair.tile2.tileType = field.tileTypes(typeIndex)
      field += pair.tile1;
      field += pair.tile2;
    }
  }
  
  def generate(field:Field) {
    field.tileTypes = TileType.LoadTileTypes(tileFile)
    // Place the full set without a tile type
    loadStructure(field, null, setupFile);
    // Set the tile types in a solvable order
    scramble(field);
  }
  
  def loadStructure(field:Field, tileType:TileType, setupFile:String) {
    field.tiles = Map()
    val source = Source.fromFile(setupFile)
    val regexTile = new Regex("^(\\d+) (\\d+) (\\d+)$", "x", "y", "z")
    val regexDims = new Regex("^(\\d+) (\\d+)$", "width", "height")
    source.getLines().foreach({ _ match {
	    case regexTile(x, y, z) => field += new Tile(x.toInt, y.toInt, z.toInt, tileType)
	    case regexDims(width, height) => field.width = width.toInt; field.height = height.toInt
    }})
    source.close
  }
  
  def extractRemovableTiles(field:Field) : List[Tile] = {
    val removables = field.tiles.filter(p => field.canMove(p._2))
    removables.foreach(field.tiles -= _._1)
    removables.map(_._2).toList
  }
}