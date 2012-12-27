package scajong.controller

import org.specs2.mutable._
import scajong.model._
import scajong.view._
import scajong.model.Setup
import scajong.util.SimpleNotification

class FakeGame(scoreFileName:String) extends Game {
  val testSetup = new Setup("Bla", "bla", "path")
  val hintPair = new TilePair(null, null)
  val sorted = List[Tile]()
  val noNextMoveTile1 = new Tile(1, 2, 3, null)
  val noNextMoveTile2 = new Tile(4, 5, 6, null)
  var nextMovePossibleResult = true
  var played = false
  var playedTiles:TilePair = null
  var newGameSetup:Setup = null
  var penaltySum = 0
  var scrambled = false
  var tileTypes = IndexedSeq[TileType]()
  var tiles = Map[Int, Tile]()
  var selected:Tile = null
  var tile:Tile = null
  def +=(tile:Tile) {}
  def -=(tile:Tile) {}
  var width = 1
  var height = 1
  val scores = new Scores(scoreFileName)
  val setups = List[Setup]()
  def setupById(id:String) = testSetup
  def play(tile1:Tile, tile2:Tile) : Boolean = {
    played = true;
    playedTiles = new TilePair(tile1, tile2);
    if (tile1 == noNextMoveTile1 && tile2 == noNextMoveTile2) {
      tiles += (0 -> tile1)
      tiles += (1 -> tile2)
      nextMovePossibleResult = false
    }
    true
  }
  def hint = hintPair
  def startNewGame(setup:Setup) { newGameSetup = setup }
  def scramble { scrambled = true }
  def canMove(tile:Tile) : Boolean = { this.tile = tile; true}
  def topmostTile(x:Int, y:Int) = tile
  def findTile(x:Double, y:Double, z:Double) = tile
  def calcTileIndex(tile:Tile) : Int = { this.tile = tile; 666 }
  def sortedTiles = sorted
  def addHintPenalty { penaltySum += Game.HintPenalty }
  def addMoveablesPenalty { penaltySum += Game.MoveablesPenalty }
  def nextMovePossible = nextMovePossibleResult
  def gameTime = 100000
  def setup:Setup = null
}

class FakeView extends View {
  var started = false
  var stopped = false
  var startedController:Controller = null
  var stoppedController:Controller = null
  var startedHint = false
  var stoppedHint = false
  var startedMoveables = false
  var stoppedMoveables = false
  var won = false
  var noMoves = false
  var tilesRemoved = false
  var tileSelected = false
  var scrambled = false
  var createdNewGame = false
  var newScoreboardEntry = false
  
  override def startView(controller:Controller) { startedController = controller; started = true; }
  override def stopView(controller:Controller) { stoppedController = controller; stopped = true; }
  
  notificationProcessor = {
    case StartHintNotification(hint) => startedHint = true
    case StopHintNotification() => stoppedHint = true
    case StartMoveablesNotification() => startedMoveables = true
    case StopMoveablesNotification() => stoppedMoveables = true
    case WonNotification(setup, ms, inScoreBoard) => won = true
    case NoFurtherMovesNotification() => noMoves = true
    case TilesRemovedNotification(tiles) => tilesRemoved = true
    case TileSelectedNotification(tile) => tileSelected = true
    case ScrambledNotification() => scrambled = true
    case CreatedGameNotification() => createdNewGame = true
    case NewScoreBoardEntryNotification(setup, position) => newScoreboardEntry = true
  }
}

class ControllerSpec extends SpecificationWithJUnit {
  
  val scoreFileName = "controller_scores_test.txt"
  
  def createTestSetup : (FakeGame, FakeView, Controller) = {
    val fakeGame = new FakeGame(scoreFileName)
    val fakeView = new FakeView
    val controller = new Controller(fakeGame) { override def closeApplication {} }
    (fakeGame, fakeView, controller)
  }
  
  "A controller" should {
    
    "be able to attach a view" in {
      val (game, view, controller) = createTestSetup
      view.started must beFalse
      view.startedController must beNull
      controller.attachView(view)
      view.started must beTrue
      view.startedController must be_==(controller)
    }
    
    "be able to detach a view" in {
      val (game, view, controller) = createTestSetup
      view.stopped must beFalse
      view.stoppedController must beNull
      controller.detachView(view)
      view.stopped must beTrue
      view.stoppedController must be_==(controller)
    }
    
    "can close views with autoClose automatically" in {
      val (game, view, controller) = createTestSetup
      val viewWithAutoclose = new FakeView { override def autoClose = true }
      controller.attachView(view)
      controller.attachView(viewWithAutoclose)
      viewWithAutoclose.stopped must beFalse
      controller.detachView(view)
      viewWithAutoclose.stopped must beTrue
    }
    
    "can select tiles" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val clicked1 = new Tile(1, 0, 0, null)
      val clicked2 = new Tile(2, 0, 0, null)
      view.tileSelected must beFalse
      game.played must beFalse
      game.playedTiles must beNull
      controller.selectTile(clicked1)
      view.tileSelected must beTrue
      controller.selectTile(clicked2)
      view.tilesRemoved must beTrue
      game.played must beTrue
      game.playedTiles.tile1 must be_==(clicked1)
      game.playedTiles.tile2 must be_==(clicked2)
      
      // Tests for unselecting
      controller.selectTile(clicked1)
      view.tileSelected = false
      controller.selectTile(null)
      view.tileSelected must beTrue
      view.tileSelected = false
      controller.selectTile(null)
      view.tileSelected must beFalse
    }
    
    "can start new games" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val setup = new Setup("id", "name", "path")
      view.createdNewGame must beFalse
      game.newGameSetup must beNull
      controller.startNewGame(setup)
      view.createdNewGame must beTrue
      game.newGameSetup must be_==(setup)
    }
    
    "can request hints" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      game.penaltySum must be_==(0)
      view.startedHint must beFalse
      view.stoppedHint must beFalse
      controller.requestHint
      view.startedHint must beTrue
      game.penaltySum must be_>=(Game.HintPenalty)
      Thread.sleep(2*Game.HintTimeout)
      view.stoppedHint must beTrue
    }
    
    "can request moveables" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      view.startedMoveables must beFalse
      view.stoppedMoveables must beFalse
      game.penaltySum must be_==(0)
      controller.requestMoveables
      view.startedMoveables must beTrue
      game.penaltySum must be_>=(Game.MoveablesPenalty)
      Thread.sleep(2*Game.HintTimeout)
      view.stoppedMoveables must beTrue
    }
    
    "can detach views" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      view.stopped must beFalse
      controller.detachView(view)
      view.stopped must beTrue
    }
    
    "can scramble" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      game.scrambled must beFalse
      view.scrambled must beFalse
      controller.scramble
      view.scrambled must beTrue
      game.scrambled must beTrue
    }
    
    "can add scores" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val setup = new Setup("id", "name", "path")
      game.scores.getScores(setup) must have size(0)
      view.newScoreboardEntry must beFalse
      controller.addScore(setup, "name", 12345)
      view.newScoreboardEntry must beTrue
      game.scores.getScores(setup) must have size(1)
      new java.io.File(scoreFileName).delete
    }
    
    "provides access to game scores" in {
      val (game, view, controller) = createTestSetup
      controller.scores must be_==(game.scores)
    }
    
    "provides access to game tiles" in {
      val (game, view, controller) = createTestSetup
      controller.tiles must be_==(game.tiles)
    }
    
    "provides access to game setups" in {
      val (game, view, controller) = createTestSetup
      controller.setups must be_==(game.setups)
    }
    
    "provides access to game tile types" in {
      val (game, view, controller) = createTestSetup
      controller.tileTypes must be_==(game.tileTypes)
    }
    
    "provides access to game field width and height" in {
      val (game, view, controller) = createTestSetup
      controller.fieldWidth must be_==(game.width)
      controller.fieldHeight must be_==(game.height)
    }
    
    "can check if tiles are moveable" in {
      val (game, view, controller) = createTestSetup
      val tile = new Tile(0, 0, 0, null)
      game.tile must beNull
      controller.canMove(tile) must beTrue
      game.tile must be_==(tile)
    }
    
    "can calculate a tile index" in {
      val (game, view, controller) = createTestSetup
      val tile = new Tile(0, 0, 0, null)
      game.tile must beNull
      controller.calcTileIndex(tile) must be_==(666)
      game.tile must be_==(tile)
    }
    
    "can find a setup by id" in {
      val (game, view, controller) = createTestSetup
      controller.setupById("test") must be_==(game.testSetup)
    }
    
    "can find a tile by coords" in {
      val (game, view, controller) = createTestSetup
      game.tile = new Tile(0, 0, 0, null)
      controller.findTile(0, 0, 0) must be_==(game.tile)
    }
    
    "can find the topmost tile" in {
      val (game, view, controller) = createTestSetup
      game.tile = new Tile(0, 0, 0, null)
      controller.topmostTile(0, 0) must be_==(game.tile)
    }
    
    "can provide a hint" in {
      val (game, view, controller) = createTestSetup
      controller.hint must be_==(game.hintPair)
    }
    
    "can provide sorted tiles" in {
      val (game, view, controller) = createTestSetup
      controller.sortedTiles must be_==(game.sorted)
    }
    
    "can detect if there are no further moves" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      controller.selectTile(game.noNextMoveTile1)
      view.tileSelected must beTrue
      controller.selectTile(game.noNextMoveTile2)
      game.played must beTrue
      game.nextMovePossible must beFalse
    }
  }
}