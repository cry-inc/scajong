package de.htwg.scajong.model

object Tile {
  val Width = 2
  val Height = 4
  val Depth = 1
}

class TilePair(val tile1:Tile, val tile2:Tile)

class Tile(val x:Int, val y:Int, val z:Int, var tileType:TileType) {
  
  def isInside(xf:Float, yf:Float) : Boolean = {
    xf >= x && yf >= y && xf < x + Tile.Width && yf < y + Tile.Height
  }
  
  def isInside(xf:Float, yf:Float, zf:Float) : Boolean = {
    isInside(xf, yf) && zf >= z && zf < z + Tile.Depth
  }

  def testPoints : Array[Point] = {
    var array : Array[Point] = new Array(Tile.Width * Tile.Height)
    var p = 0
    for (xp <- 0 until Tile.Width; yp <- 0 until Tile.Height) {
      array(p) = new Point(x + xp, y + yp);
      p += 1
    }
    array
  }
}