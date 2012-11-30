package scajong.model

import org.specs2.mutable._

class TileTypeSpec extends SpecificationWithJUnit {
  
  "A TileType" should {
    val tileType = new TileType(23, "t42")
    
    "have a id" in {
      tileType.id must be_==(23)
    }
    
    "have a name" in {
      tileType.name must be_==("t42")
    }
    
    "be a string" in {
      tileType.toString must be_==("TileType[23,\"t42\"]")
    }
    
    "load tile types from a file" in {
      val types = TileType.LoadTileTypes("tiles.txt")
      types must have size(47)
    }
  }
}