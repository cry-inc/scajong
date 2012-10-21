package de.htwg.scajong.model

import scala.io.Source

object TileType {
  def LoadTileTypes(filePath:String) : Array[TileType] = {
    
    val source = io.Source.fromFile(filePath)
	val lines = source.getLines()
	source.close()

    var array: Array[TileType] = new Array(lines.length)
    var i = 0
    
    for(line <- lines) {
      array(i) = new TileType(i, line)
      i += 1
    }
    
    return array
  }  
}

class TileType(val id:Int, val name:String)