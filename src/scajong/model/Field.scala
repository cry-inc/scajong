package scajong.model

import swing.Publisher
import swing.event.Event

object Field {
  val Width = 40
  val Height = 36
}

class WonEvent(val seconds:Int) extends Event
class NoFurtherMovesEvent extends Event
class FieldChangedEvent extends Event

class TileAddedEvent(val tile:Tile) extends FieldChangedEvent
class TileRemovedEvent(val tile:Tile) extends FieldChangedEvent
class ScrambledEvent extends FieldChangedEvent
class SelectedChangedEvent(val tile:Tile) extends FieldChangedEvent

class Field(generator:IGenerator) extends Publisher {
  var tiles : Map[Int, Tile] = Map()
  var tileTypes : Array[TileType] = Array()
  private var _selected:Tile = null

  generator.generate(this)
  
  def selected = _selected
  
  def selected_=(newSelected:Tile) {
    _selected = newSelected;
    publish(new SelectedChangedEvent(_selected))
  }

  private def calcTileIndex(tile:Tile):Int = {
    calcTileIndex(tile.x, tile.y, tile.z)
  }
 
  private def calcTileIndex(x:Int, y:Int, z:Int):Int = {
	z * Field.Width * Field.Height + y * Field.Width + x
  }
  
  def +=(tile:Tile) {
    tiles += (calcTileIndex(tile) -> tile)
    publish(new TileAddedEvent(tile))
  }
  
  def -=(tile:Tile) {
    tiles -= calcTileIndex(tile)
    publish(new TileRemovedEvent(tile))
  }
  
  def scramble {
    generator.scramble(this)
    publish(new ScrambledEvent)
  }

  implicit val tileOrdering = Ordering.by((t: Tile) => (t.z, t.y, t.x))
  
  def getSortedTiles() : Array[Tile] = {
    val list = tiles.map(_._2).toList
    list.sorted.toArray
  }
  
  def possibleTileIndices(x:Float, y:Float, z:Float) : IndexedSeq[Int] = {
    val ix = math.floor(x).toInt
    val iy = math.floor(y).toInt
    val iz = math.floor(z).toInt
    for (i <- 0 until Tile.Width; j <- 0 until Tile.Height) yield {
      calcTileIndex(ix - i, iy - j, iz)
    }
}

  def findTile(x:Float, y:Float, z:Float) : Tile = {
    val indices = possibleTileIndices(x, y, z)
    val foundTiles = tiles.filter(p => indices.contains(p._1) && p._2.isInside(x, y, z))
    if (foundTiles.nonEmpty) foundTiles.last._2 else null
  }
  
  private def canMove(tile:Tile, xd:Int, yd:Int, zd:Int) : Boolean = {
  	val points = tile.testPoints
  	val z = tile.z + zd
  	points.forall(point => {
  	  val x = point.x + xd
      val y = point.y + yd
      val found = findTile(x, y, z)
      (found == null || tile == found)
  	}) 
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
  
  def play(tile1:Tile, tile2:Tile) : Boolean = {
    if (tile1 == tile2)
      false
    else if (tile1.tileType != tile2.tileType)
      false
    else if (!canMove(tile1) || !canMove(tile2))
      false
    else {
  	  -=(tile1)
  	  -=(tile2);
	  if (tiles.size == 0)
	    //TODO: add game time
	   	publish(new WonEvent(123))
	  else if (!nextMovePossible)
		  publish(new NoFurtherMovesEvent)
	    true
    }
  }
  
  def getHint : TilePair = {
    var moveableTiles = tiles.map(_._2).filter(canMove(_))
    for (i <- moveableTiles; j <- moveableTiles) {
      if (i != j && j.tileType == i.tileType)
        return new TilePair(i, j)
    }
    null
  }
}