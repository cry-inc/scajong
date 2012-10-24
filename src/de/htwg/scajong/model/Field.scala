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
  var tileTypes : Array[TileType] = Array()
  
  generator.generate(this)

  private def calcTileIndex(tile:Tile):Int = {
	calcTileIndex(tile.x, tile.y, tile.z)
  }
 
  private def calcTileIndex(x:Int, y:Int, z:Int):Int = {
	z * Field.Width * Field.Height + y * Field.Width + x
  }
  
  private def sortCriteria(tile1:Tile, tile2:Tile) : Boolean = {
    if (tile1.z == tile2.z)
    {
      if (tile1.y == tile2.y)
        tile1.x < tile2.x
      else
        tile1.y < tile2.y
    }
    else
      tile1.z < tile2.z
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
    list.sort(sortCriteria).toArray
  }
  
  def possibleTileIndices(x:Float, y:Float, z:Float) : Array[Int] = {
    val ix = math.floor(x).toInt
    val iy = math.floor(y).toInt
    val iz = math.floor(z).toInt
    var indicies = new Array[Int](Tile.Width * Tile.Height)
    var c = 0
    for (i <- 0 until Tile.Width; j <- 0 until Tile.Height) {
      indicies(c) = calcTileIndex(ix - i, iy - j, iz)
      c += 1
    }
    indicies
}

  def findTile(x:Float, y:Float, z:Float) : Tile = {
    var tile:Tile = null
    val indices = possibleTileIndices(x, y, z)
    for (i <- indices)
      if (tiles.contains(i) && tiles(i).isInside(x, y, z))
        tile = tiles(i)
    return tile;
  }
  
  private def canMove(tile:Tile, xd:Int, yd:Int, zd:Int) : Boolean = {
	var points = tile.testPoints
	val z = tile.z + zd
	for (i <- 0 until points.length) {
	  val x = points(i).x + xd
	  val y = points(i).y + yd
	  val found = findTile(x, y, z)
	  if (found != null && tile != found)
	    return false
	}
	true    
  }

  private def canMoveUp(tile:Tile) : Boolean = {
    canMove(tile, 0, 0, 1)
  }
  
  private def canMoveRight(tile:Tile) : Boolean = {
    canMove(tile, 1, 0, 0)
  }
    
  private def canMoveLeft(tile:Tile) : Boolean = {
    canMove(tile, -1, 0, 0)
  }

  def canMove(tile:Tile) : Boolean = {
    val up = canMoveUp(tile)
    val ul = up && canMoveLeft(tile)
    val ur = up && canMoveRight(tile)
    ul || ur
  }
  
  def nextMovePossible : Boolean = {
    getHint != null
  }
  
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
	  if (tiles.size == 0)
	    PlayResult.Won
	  else {
		if (!nextMovePossible)
		  PlayResult.NoFurtherMoves
		else
		  PlayResult.ValidMove
	  }
    }
  }
  
  def getHint : TilePair = {
    var moveableTiles : List[Tile] = Nil
    
    for (pair <- tiles)
      if (canMove(pair._2))
        moveableTiles = pair._2 :: moveableTiles

    for (i <- 0 until moveableTiles.length; j <- 0 until moveableTiles.length) {
      if (i != j && moveableTiles(i).tileType == moveableTiles(j).tileType)
        return new TilePair(moveableTiles(i), moveableTiles(j))
    }

    null
  }
}