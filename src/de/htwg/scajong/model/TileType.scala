package de.htwg.scajong.model

import scala.io.Source

object TileType {
  
  // D:/Dev/Scala/ScaJong/tiles.txt
  def LoadTileTypes(filePath:String) : List[TileType] = {
    var list : List[TileType] = Nil
    val source = io.Source.fromFile(filePath)
	var id = 0
    for (line <- source.getLines) {
      list = new TileType(id, line) :: list
      id += 1
    }
    source.close()
    list
  }  
}

class TileType(val id:Int, val name:String) {
  override def toString = {
    "{" + id.toString + ": \"" + name + "\"}"
  }
}