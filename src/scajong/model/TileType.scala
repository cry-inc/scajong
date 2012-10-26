package scajong.model

object TileType {
  def LoadTileTypes(filePath:String) : Array[TileType] = {
    var list : List[TileType] = Nil
    val source = io.Source.fromFile(filePath)
	var id = 0
    for (line <- source.getLines) {
      list = new TileType(id, line) :: list
      id += 1
    }
    source.close()
    list.toArray
  }  
}

class TileType(val id:Int, val name:String) {
  override def toString = {
    "TileType[" + id.toString + ",\"" + name + "\"]"
  }
}