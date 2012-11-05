package scajong.model

import swing.Publisher
import swing.event.Event
import io.Source
import java.io.File

class WonEvent(val seconds:Int) extends Event
class NoFurtherMovesEvent extends Event
class TilesChangedEvent extends Event
class ScrambledEvent extends TilesChangedEvent
class SelectedChangedEvent(val tile:Tile) extends TilesChangedEvent
class StartGameEvent extends Event
class InStartMenuChangedEvent(val inMenu:Boolean) extends Event

class Field(setupsDir:String, tileFile:String, generator:IGenerator) extends Publisher {
  var width = 40
  var height = 26
  var tiles = Map[Int, Tile]()
  val tileTypes = TileType.LoadTileTypes(tileFile)
  val setups = listSetups
  private var _inStartMenu = true
  private var _selected:Tile = null
  
  def inStartMenu = _inStartMenu
  
  def inStartMenu_=(newValue:Boolean) {
    _inStartMenu = newValue;
    publish(new InStartMenuChangedEvent(newValue))
  }
  
  def selected = _selected
  
  def selected_=(newSelected:Tile) {
    _selected = newSelected;
    publish(new SelectedChangedEvent(_selected))
  }
  
  def calcTileIndex(tile:Tile):Int = {
    calcTileIndex(tile.x, tile.y, tile.z)
  }
  
  def calcTileIndex(x:Int, y:Int, z:Int) : Int = {
    z * width * height + y * width + x
  }
  
  def +=(tile:Tile) {
    tiles += (calcTileIndex(tile) -> tile)
    publish(new TilesChangedEvent)
  }
  
  def -=(tile:Tile) {
    tiles -= calcTileIndex(tile)
    publish(new TilesChangedEvent)
  }
  
  def scramble {
    generator.scramble(this)
    publish(new ScrambledEvent)
  }

  def getSortedTiles() : Array[Tile] = {
    val list = tiles.map(_._2).toList
    implicit val tileOrdering = Ordering.by((t: Tile) => (t.z, t.y, t.x))
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
  
  def topmostTile(x:Int, y:Int) : Tile = {
    val stack = tiles.filter(p => p._2.isInside(x, y)).map(_._2).toList
    if (stack.nonEmpty) stack.sortWith((a,b) => a.z < b.z).last else null
  }
  
  private def canMove(tile:Tile, xd:Int, yd:Int, zd:Int) : Boolean = {
  	val z = tile.z + zd
  	tile.testPoints.forall(point => {
  	  val x = point.x + xd
      val y = point.y + yd
      val found = findTile(x, y, z)
      (found == null || tile == found)
  	}) 
  }

  private def canMoveUp(tile:Tile) = canMove(tile, 0, 0, 1)
  private def canMoveRight(tile:Tile) = canMove(tile, 1, 0, 0)
  private def canMoveLeft(tile:Tile) = canMove(tile, -1, 0, 0)

  def canMove(tile:Tile) : Boolean = {
    val up = canMoveUp(tile)
    val ul = up && canMoveLeft(tile)
    val ur = up && canMoveRight(tile)
    ul || ur
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
    // TODO: rewrite without return
    var moveableTiles = tiles.map(_._2).filter(canMove(_))
    for (i <- moveableTiles; j <- moveableTiles) {
      if (i != j && j.tileType == i.tileType)
        return new TilePair(i, j)
    }
    null
  }
  
  def nextMovePossible : Boolean = getHint != null
  
  private def getSetupName(setupFile:String) = {
    val source = io.Source.fromFile(setupFile)
    val lines = source.getLines.map(f => f).toList
    source.close
    lines(0)
  }
  
  private def listSetups = {
    val fileArray = new File(setupsDir).listFiles
    val fileNames = fileArray.map(f => f.getPath)
    val filtered = fileNames.filter(_.endsWith(".txt"))
    filtered.map(f => (f, getSetupName(f))).toMap
  }
  
  def startNewGame(setupFile:String) {
    generator.generate(this, setupFile)
    publish(new StartGameEvent)
  }
}