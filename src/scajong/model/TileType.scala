package scajong.model

import scajong.util.FileUtil

object TileType {
  def LoadTileTypes(filePath:String) : Array[TileType] = {
    var list : List[TileType] = Nil
    val lines = FileUtil.readLines(filePath)
    var id = 0
    for (line <- lines) {
      list = new TileType(id, line) :: list
      id += 1
    }
    list.toArray
  }  
}

class TileType(val id:Int, val name:String) {
  override def toString = {
    "TileType[" + id.toString + ",\"" + name + "\"]"
  }
}