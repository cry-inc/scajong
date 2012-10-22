package de.htwg.scajong.model

import scala.util.Sorting$

object PlayResult extends Enumeration {
  type PlayResult = Value
  val DifferentTypes = Value("DifferentTypes")
  val CanNotMoveTile = Value("CanNotMoveTile")
  val ValidMove = Value("ValidMove")
  val InvalidMove = Value("InvalidMove")
  val NoFurtherMoves = Value("NoFurtherMoves")
  val Won = Value("Won")
}

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
 
  private def sortCriteria(tile1:Tile, tile2:Tile) : Boolean = {
    if (tile1.z == tile2.z)
    {
      if (tile1.y == tile2.y)
        tile1.x > tile2.x
      else
        tile1.y > tile2.y
    }
    else
      tile1.z > tile2.z
  }
  
  def +=(tile:Tile) {
    tiles += (calcTileIndex(tile) -> tile)
  }
  
  def -=(tile:Tile) {
    tiles -= calcTileIndex(tile)
  }
  
  def scramble {
    generator.scramble(this)
  }
  
  def getSortedTiles() : Array[Tile] = {
    
    var list:List[Tile] = Nil
    for (tile <- tiles.iterator)
      list = tile._2 :: list
    list.sort(sortCriteria)
    list.toArray
  }
  
  def canMove(tile:Tile) : Boolean = {
    // TODO: implement
    true
  }
  
  def nextMovePossible : Boolean = {
    //TODO: implement
    true
  }
  
  // TODO: remove somehow?
  private def countCriteria(a:(Int, Tile)) = true
  
  def play(tile1:Tile, tile2:Tile) : PlayResult.Value = {
    if (tile1 == tile2)
      PlayResult.InvalidMove
    else if (tile1.tileType != tile2.tileType)
      PlayResult.DifferentTypes
    else if (!canMove(tile1) || !canMove(tile2))
      PlayResult.CanNotMoveTile
    else {
	  -=(tile1)
	  -=(tile2);
	  if (tiles.count(countCriteria) == 0)
	    PlayResult.Won
	  else {
		if (!nextMovePossible)
		  PlayResult.NoFurtherMoves
		else
		  PlayResult.ValidMove
	  }
    }
  }
}