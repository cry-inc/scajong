package de.htwg.scajong.model

object Tile {
  val Width = 2
  val Height = 4
  val Depth = 1
}

class Tile(val x:Int, val y:Int, val z:Int, var tileType:TileType) {
  
  def isInside(xf:Float, yf:Float) : Boolean = {
    xf >= x && yf >= y && xf < x + Tile.Width && yf < y + Tile.Height
  }
  
  def isInside(xf:Float, yf:Float, zf:Float) : Boolean = {
    isInside(xf, yf) && zf >= z && zf < z + Tile.Depth
  }

}