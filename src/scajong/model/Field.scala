package scajong.model

import scajong.util._
import io.Source
import java.io.File
import java.sql.Date

class WonNotification(val setup:String, val ms:Int) extends SimpleNotification
class NoFurtherMovesNotification extends SimpleNotification
class TilesChangedNotification extends SimpleNotification
class ScrambledNotification extends SimpleNotification
class SelectedTileNotification(val tile:Tile) extends SimpleNotification
class CreatedGameNotification extends SimpleNotification

class Field(setupsDir:String, tileFile:String, generator:IGenerator) extends SimplePublisher {
  var width = 40
  var height = 26
  var tiles = Map[Int, Tile]()
  val tileTypes = TileType.LoadTileTypes(tileFile)
  val setups = listSetups
  val scores = new Scores("scores.txt")
  private var _selected:Tile = null
  private var currentSetup = new String
  private var startTime : Long = 0
  private var sendTileChangedEvent = true

  def selected = _selected
  
  def selected_=(newSelected:Tile) {
    _selected = newSelected;
    sendNotification(new SelectedTileNotification(_selected))
  }
  
  def calcTileIndex(tile:Tile):Int = {
    calcTileIndex(tile.x, tile.y, tile.z)
  }
  
  def calcTileIndex(x:Int, y:Int, z:Int) : Int = {
    z * width * height + y * width + x
  }
  
  def +=(tile:Tile) {
    tiles += (calcTileIndex(tile) -> tile)
    if (sendTileChangedEvent) {
	    sendNotification(new TilesChangedNotification)
    }
  }
  
  def -=(tile:Tile) {
    tiles -= calcTileIndex(tile)
    if (sendTileChangedEvent) {
	    sendNotification(new TilesChangedNotification)
    }
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
    if (startTime == 0)
      startTime = System.currentTimeMillis
    if (tile1 == tile2)
      false
    else if (tile1.tileType != tile2.tileType)
      false
    else if (!canMove(tile1) || !canMove(tile2))
      false
    else {
  	  -=(tile1)
  	  -=(tile2);
		  if (tiles.size == 0) {
		    val elapsed:Int = (System.currentTimeMillis - startTime).toInt
		   	sendNotification(new WonNotification(currentSetup, elapsed))
		  } else if (!nextMovePossible)
			  sendNotification(new NoFurtherMovesNotification)
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
  
  def scramble {
    sendTileChangedEvent = false
    generator.scramble(this)
    sendTileChangedEvent = true
    sendNotification(new ScrambledNotification)
  }
  
  def startNewGame(setupFile:String, setupName:String) {
    sendTileChangedEvent = false
    generator.generate(this, setupFile)
    sendTileChangedEvent = true
    startTime = 0
    currentSetup = setupName
    sendNotification(new CreatedGameNotification)
  }
}