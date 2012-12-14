package scajong.model

import scajong.util.SimplePublisher;
import scajong.util.SimpleNotification;

object Game {
  val HintPenalty = 15000
  val HintTimeout = 3000
  val MoveablesPenalty = 5000
  val MoveablesTimeout = 5000
}

trait Game {
  
  // Tiles & TileTypes
  def tileTypes:IndexedSeq[TileType]
  def tiles:Map[Int, Tile]
  def tiles_=(newTiles:Map[Int, Tile])
  def +=(tile:Tile)
  def -=(tile:Tile)
  
  // Field 
  def width:Int
  def width_=(newWidth:Int)
  def height:Int
  def height_=(newHeight:Int)
  
  // Scores & setups
  def scores:Scores
  def setups:List[Setup]
  def setupById(id:String) : Setup
  def setup : Setup
  
  // Game logic
  def play(tile1:Tile, tile2:Tile) : Boolean
  def hint:TilePair
  def startNewGame(setup:Setup)
  def scramble
  def addHintPenalty
  def addMoveablesPenalty
  def gameTime:Int
  
  // Tile logic
  def canMove(tile:Tile) : Boolean
  def topmostTile(x:Int, y:Int) : Tile
  def findTile(x:Double, y:Double, z:Double) : Tile;
  def calcTileIndex(x:Int, y:Int, z:Int) : Int
  def calcTileIndex(tile:Tile) : Int
  def sortedTiles:List[Tile]
  def nextMovePossible:Boolean
}