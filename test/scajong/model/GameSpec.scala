package scajong.model

import org.specs2.mutable._
import scajong.util._
import scajong.util.SimpleNotification

class FakeSubscriber extends SimpleSubscriber {
	var started = false
	var tilesChanged = false
	var scrambled = false
	var won = false
	var selected = false
	def processNotifications(sn: SimpleNotification) {
		sn match {
		  case n:TilesChangedNotification => tilesChanged = true
		  case n:CreatedGameNotification => started = true
		  case n:ScrambledNotification => scrambled = true
		  case n:WonNotification => won = true
		  case n:SelectedTileNotification => selected = true
		}
	}
}

class GameSpec extends SpecificationWithJUnit {
	
  "A Game" should {
  	
  	def createTestObjects : (Game, Setup, FakeSubscriber) = {
			val game = new Game("setups/", "tiles.txt", new ReverseGenerator)
			val testSetup = game.setups.filter(_.id == "test")(0)
			val subscriber = new FakeSubscriber
			game.addSubscriber(subscriber)
			(game, testSetup, subscriber)
  	}

  	"have setups" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.setups.length must be_>(0)
  	}
  	
  	"have tile types" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.tileTypes.length must be_>(0)
  	}
  	
  	"can create a new test game" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  subscriber.started must beFalse
  	  game.startNewGame(testSetup)
  	  subscriber.started must beTrue
  	  game.tiles must have size(4)
  	  game.selected must be_==(null)
  	}
  	
  	"can select tiles" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val tile = game.tiles.head._2
  	  subscriber.selected must beFalse
  	  game.selected = tile
  	  game.selected must be_==(tile)
  	  subscriber.selected must beTrue
  	}
  	
  	"can calculate a tile index" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  // Width: 6, Height: 10
  	  game.calcTileIndex(1, 0, 0) must be_==(1)
  	  game.calcTileIndex(1, 1, 0) must be_==(7)
  	  game.calcTileIndex(1, 1, 1) must be_==(67)
  	  val tile = new Tile(2, 1, 0, null)
  	  game.calcTileIndex(tile) must be_==(8)
  	}
  	
  	"can have tiles added and removed" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val tile = new Tile(0, 0, 0, null)
  	  game.tiles must have size(4)
  	  subscriber.tilesChanged must beFalse
  	  game += tile
  	  subscriber.tilesChanged must beTrue
  	  game.tiles must have size(5)
  	  subscriber.tilesChanged = false
  	  game -= tile
  	  subscriber.tilesChanged must beTrue
  	  game.tiles must have size(4)
  	}
  	
  	"provides sorted tiles" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val sorted = game.sortedTiles
  	  sorted must have size(4)
  	  // Should sort from left top to bottom right
  	  sorted.last.x > sorted.head.x must beTrue
  	  sorted.last.y > sorted.head.y must beTrue
  	}
  	
  	"can find tiles by coords" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val tile = game.tiles.head._2
  	  val xc = tile.x + Tile.Width / 2 + 0.01f
  	  val yc = tile.y + Tile.Height / 2 + 0.01f
  	  val zc = tile.z + Tile.Depth / 2 + 0.01f
  	  val found1 = game.findTile(xc, yc, zc)
  	  found1 must be_==(tile)
  	  val found2 = game.findTile(0.01f, 0.01f, 0.01f)
  	  found2 must be_==(null)
  	}
  	
  	"can detected (un)moveable tiles" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val oldTile = game.tiles.head._2
  	  // Create new tile on top of the four from the test setup
  	  val newTile = new Tile(2, 3 , 1, game.tileTypes(0))
  	  game.canMove(oldTile) must beTrue
  	  game += newTile
  	  game.canMove(newTile) must beTrue
  	  game.canMove(oldTile) must beFalse
  	}
  	
  	"can play a tile pair" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val hint = game.hint
  	  val oldCount = game.tiles.size
  	  game.play(hint.tile1, hint.tile2)
  	  game.tiles.size must be_==(oldCount-2)
  	}
  	
  	"can provide a hint" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  val hint = game.hint
  	  hint.tile1.tileType must be_==(hint.tile2.tileType)
  	  game.canMove(hint.tile1) must beTrue
  	  game.canMove(hint.tile2) must beTrue
  	}
  	
  	"can scramble the game" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  subscriber.scrambled must beFalse
  	  game.scramble
  	  subscriber.scrambled must beTrue
  	}
  	
  	"provide a won notification" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  subscriber.won must beFalse
  	  while (game.tiles.size > 0) {
	  	  val hint = game.hint
	  	  game.play(hint.tile1, hint.tile2)
  	  }
  	  subscriber.won must beTrue
  	}
  	
  	"can get a setup by id" in {
  	  val (game, testSetup, subscriber) = createTestObjects
  	  game.setupById("test") must be_==(testSetup)
  	}
  	
  	"can find out if there is a possible move" in {
			val (game, testSetup, subscriber) = createTestObjects
  	  game.startNewGame(testSetup)
  	  game.nextMovePossible must beTrue
  	}
	}
}