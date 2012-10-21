package de.htwg.scajong.model

object Field {
  val Width = 40
  val Height = 36
}

class Field(generator:IGenerator) {
  
  var tiles : Map[Int, Tile] = Map()
  var tileTypes : Array[TileType] = new Array(0)
  
  generator.generate(this)

  private def calcTileIndex(tile:Tile):Int = {
	tile.z * Field.Width * Field.Height + tile.y * Field.Width + tile.x
  }
  
  def +=(tile:Tile) {
    tiles += (calcTileIndex(tile) -> tile)
  }
  
  def -=(tile:Tile) {
    tiles -= calcTileIndex(tile)
  }
  
  def getSortedTiles() : Array[Tile] = {
    //TODO
    new Array(0)
  }
}