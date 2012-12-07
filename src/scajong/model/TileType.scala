package scajong.model

import scajong.util.FileUtil

object TileType {
  def LoadTileTypes(filePath:String) : IndexedSeq[TileType] = {
    val lines = FileUtil.readLines(filePath)
    for (i <- 0 until lines.length) yield {
      new TileType(i, lines(i))
    }
  }
}

class TileType(val id:Int, val name:String) {
  override def toString = {
    "TileType[" + id.toString + ",\"" + name + "\"]"
  }
}