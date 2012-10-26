package scajong.model

import scala.util.Random

class ReverseGenerator(val setupFile:String, val tileFile:String) extends IGenerator {
  
  def scramble(field:Field) {
    var reversed : List[TilePair] = Nil
    while (field.tiles.size > 0) {
      
      // Find two or more random outer tiles, remove them and store the coordinates
      var removables = extractRemovableTiles(field)
      
      // Continue until no more removable tile pairs are left in the list
      while (removables.length > 1) {
        reversed = new TilePair(removables(0), removables(1)) :: reversed
        removables = removables.drop(2)
      }
      
      // Add remaining tile to the field
      for (tile <- removables)
        field += tile
    }    
    
    // Read the list from behind and and get random a random tile type for each pair to build the game
    val random = new Random
    for (pair <- reversed) {
      val typeIndex = random.nextInt(field.tileTypes.length)
      pair.tile1.tileType = field.tileTypes(typeIndex)
      pair.tile2.tileType = field.tileTypes(typeIndex)
      field += pair.tile1;
      field += pair.tile2;
    }
  }
  
  def generate(field:Field) {
    field.tileTypes = TileType.LoadTileTypes(tileFile)
    field.tiles = Map()
    
    // Place the full set without a tile type
    loadStructure(field, null, setupFile);

    // Set the tile types in a solvable order
    scramble(field);
  }
  
  def loadStructure(field:Field, tileType:TileType, setupFile:String) {
    val source = io.Source.fromFile(setupFile)
    for (line <- source.getLines) {
      val splitted = line.split(' ')
      if (splitted.length == 3) {
        val x = splitted(0).toInt
        val y = splitted(1).toInt
        val z = splitted(2).toInt
        field += new Tile(x, y, z, tileType)
      }
    }
    source.close()  
  }
  
  def extractRemovableTiles(field:Field) : List[Tile] = {
    val removables = field.tiles.filter(p => field.canMove(p._2))
    removables.foreach(field.tiles -= _._1)
    removables.map(_._2).toList
  }
}