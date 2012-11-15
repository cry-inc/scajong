package scajong.model

import org.specs2.mutable._

class TileSpec extends SpecificationWithJUnit {
	
  "A Point" should {
    val point = new Point(11, 12, -13)
    
    "have an x value" in {
      point.x must be_==(11)
    }
    
    "have a y value" in {
      point.y must be_==(12)
    }
    
    "have a z value" in {
      point.z must be_==(-13)
    }
  }
  
  "A Tile" should {
		val tile = new Tile(1, 2, 3, null)
	  
	  "have an x value" in {
			tile.x must be_==(1)
		}
		
		"have a y value" in {
			tile.y must be_==(2)
		}

		"have a z value" in {
			tile.z must be_==(3)
		}
		
		"be able to change the tileType" in {
		  val tileType = new TileType(23, "t42")
		  tile.tileType = tileType
		  tile.tileType.id must be_==(23)
		  tile.tileType.name must be_==("t42")
		}
		
		"be a string" in {
		  tile.toString must be_==("Tile[1,2,3,TileType[23,\"t42\"]]")
		}
		
		"have test points" in {
		  val points = tile.testPoints
		  points must have size(Tile.Width * Tile.Height * Tile.Depth)
		}
		
		"have a point outside" in {
			tile.isInside(0, 0, 0) must be_==(false)
		}
		
		"have a point inside" in {
			tile.isInside(2, 3, 3) must be_==(true)
		}
	}
  
  "A TilePair" should {
    val tile1 = new Tile(1, 2, 3, null)
    val tile2 = new Tile(4, 5, 6, null)
    val pair = new TilePair(tile1, tile2)
    
    "have a tile1 value" in {
      pair.tile1 must be_==(tile1)
    }
    
    "have a tile2 value" in {
      pair.tile2 must be_==(tile2)
    }
  }
}