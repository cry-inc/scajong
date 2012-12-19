package scajong.model

object Game {
  val HintPenalty = 15000
  val HintTimeout = 3000
  val MoveablesPenalty = 5000
  val MoveablesTimeout = 5000
}

trait Game {

  // Field 
  def width:Int
  def width_=(newWidth:Int)
  def height:Int
  def height_=(newHeight:Int)

  // Tiles
  def tileTypes:IndexedSeq[TileType]
  def tiles:Map[Int, Tile]
  def tiles_=(newTiles:Map[Int, Tile])
  def +=(tile:Tile)
  def -=(tile:Tile)
  def canMove(tile:Tile) : Boolean
  def topmostTile(x:Int, y:Int) : Tile
  def findTile(x:Double, y:Double, z:Double) : Tile;
  def calcTileIndex(tile:Tile) : Int
  def sortedTiles:List[Tile]
  def hint:TilePair
  def gameTime:Int
  def nextMovePossible:Boolean

  // Scores & Setups
  def scores:Scores
  def setups:List[Setup]
  def setupById(id:String) : Setup
  def setup : Setup

  // Game actions
  def play(tile1:Tile, tile2:Tile) : Boolean
  def startNewGame(setup:Setup)
  def scramble
  def addHintPenalty
  def addMoveablesPenalty
}