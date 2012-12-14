package scajong.model

import scajong.util.SimplePublisher;
import scajong.util.SimpleNotification;

case class WonNotification(val setup:Setup, val ms:Int, val inScoreBoard:Boolean) extends SimpleNotification
case class NoFurtherMovesNotification() extends SimpleNotification
case class TilesChangedNotification() extends SimpleNotification
case class ScrambledNotification() extends SimpleNotification
case class SelectedTileNotification(val tile:Tile) extends SimpleNotification
case class CreatedGameNotification() extends SimpleNotification
case class NewScoreBoardEntryNotification(val setup:Setup, val position:Int) extends SimpleNotification

object Game {
  val HintPenalty = 15000
  val HintTimeout = 3000
  val MoveablesPenalty = 5000
  val MoveablesTimeout = 5000
}

trait Game extends SimplePublisher {
  
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
  
  // Game logic
  def play(tile1:Tile, tile2:Tile) : Boolean // can send TilesChanged, Won or NoFurtherMoves notifications
  def hint : TilePair
  def startNewGame(setup:Setup) // sends CreatedGame notification
  def scramble // sends Scrambled notification
  def selected:Tile
  def selected_=(tile:Tile) // sends SelectedTile notification
  def requestHint:(TilePair,Int) // the user wants a hint. also adds the according penalty
  def requestMoveables:Int // the user wants a moveables hint. adds also a penalty
  
  // Tile logic
  def canMove(tile:Tile) : Boolean
  def topmostTile(x:Int, y:Int) : Tile
  def findTile(x:Double, y:Double, z:Double) : Tile;
  def calcTileIndex(x:Int, y:Int, z:Int) : Int
  def calcTileIndex(tile:Tile) : Int
  def sortedTiles:List[Tile]
}