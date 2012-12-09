package scajong.model

import scajong.util._
import io.Source

object GameImplementation {
  def create(scoreFile:String = "scores.txt", tilesFile:String = "tiles.txt", setupsDir:String = "setups/") : Game = {
    // TODO: construct setups and tiles in seperate steps
    val generator:Generator = new ReverseGenerator
    new GameImplementation(scoreFile, setupsDir, tilesFile, generator)
  }
}

class GameImplementation private (scoreFile:String, setupsDir:String, tileFile:String, generator:Generator) extends Game {
  var width = 40
  var height = 26
  var tiles = Map[Int, Tile]()
  val tileTypes = TileType.LoadTileTypes(tileFile)
  val setups = Setup.CreateSetupsList(setupsDir)
  val scores = new Scores(scoreFile, this)
  private var _selected:Tile = null
  private var currentSetup:Setup = null
  private var startTime : Long = 0
  private var sendTileChangedEvent = true
  private var penalty = 0

  def selected = _selected

  def selected_=(newSelected:Tile) {
    _selected = newSelected;
    sendNotification(new SelectedTileNotification(_selected))
  }

  def calcTileIndex(tile:Tile) : Int = {
    calcTileIndex(tile.x, tile.y, tile.z)
  }

  def calcTileIndex(x:Int, y:Int, z:Int) = {
    z * width * height + y * width + x
  }

  def addPenalty(ms:Int) {
    if (ms > 0) penalty += ms
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

  def sortedTiles() : List[Tile] = {
    val list = tiles.map(_._2).toList
    implicit val tileOrdering = Ordering.by((t: Tile) => (t.z, t.y, t.x))
    list.sorted
  }

  def possibleTileIndices(x:Double, y:Double, z:Double) : IndexedSeq[Int] = {
    val ix = math.floor(x).toInt
    val iy = math.floor(y).toInt
    val iz = math.floor(z).toInt
    for (i <- 0 until Tile.Width; j <- 0 until Tile.Height) yield {
      calcTileIndex(ix - i, iy - j, iz)
    }
  }

  def findTile(x:Double, y:Double, z:Double) : Tile = {
    val indices = possibleTileIndices(x, y, z)
    val foundTiles = tiles.filter(p => indices.contains(p._1) && p._2.isInside(x, y, z))
    if (foundTiles.nonEmpty) foundTiles.last._2 else null
  }

  def topmostTile(x:Int, y:Int) : Tile = {
    val stack = tiles.filter(p => p._2.isInside(x, y)).map(_._2).toList
    if (stack.nonEmpty) stack.sortWith(_.z < _.z).last else null
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
        val time = (System.currentTimeMillis - startTime).toInt + penalty
        val inScoreBoard = scores.isInScoreboard(currentSetup, time)
         sendNotification(new WonNotification(currentSetup, time, inScoreBoard))
      } else if (!nextMovePossible)
        sendNotification(new NoFurtherMovesNotification)
      true
    }
  }

  def hint : TilePair = {
    // TODO: rewrite without return
    var moveableTiles = tiles.map(_._2).filter(canMove(_))
    for (i <- moveableTiles; j <- moveableTiles) {
      if (i != j && j.tileType == i.tileType)
        return new TilePair(i, j)
    }
    null
  }

  def nextMovePossible : Boolean = hint != null

  def setupById(setupId:String) = {
    val filtered = setups.filter(_.id == setupId)
    if (filtered.length == 1)
      filtered(0)
    else
      null
  }

  def scramble {
    sendTileChangedEvent = false
    generator.scramble(this)
    sendTileChangedEvent = true
    sendNotification(new ScrambledNotification)
  }

  def startNewGame(setup:Setup) {
    sendTileChangedEvent = false
    generator.generate(this, setup.path)
    sendTileChangedEvent = true
    startTime = 0
    penalty = 0
    currentSetup = setup
    sendNotification(new CreatedGameNotification)
  }
}