package scajong.controller

import org.specs2.mutable._
import scajong.model._
import scajong.view._
import scajong.model.Setup
import scajong.util.SimpleNotification

class FakeGame(scoreFileName:String) extends Game {
  var played = false
  var playedTiles:TilePair = null
  var newGameSetup:Setup = null
  var penaltySum = 0
  var scrambled = false
  var tileTypes = IndexedSeq[TileType]()
  var tiles = Map[Int, Tile]()
  var selected:Tile = null
  def +=(tile:Tile) {}
  def -=(tile:Tile) {}
  var width = 1
  var height = 1
  val scores = new Scores(scoreFileName)
  val setups = List[Setup]()
  def setupById(id:String) : Setup = null
  def play(tile1:Tile, tile2:Tile) : Boolean = {
    played = true;
    playedTiles = new TilePair(tile1, tile2);
    true
  }
  def hint = new TilePair(null, null)
  def startNewGame(setup:Setup) { newGameSetup = setup }
  def scramble { scrambled = true }
  def canMove(tile:Tile) : Boolean = true
  def topmostTile(x:Int, y:Int) : Tile = null
  def findTile(x:Double, y:Double, z:Double) : Tile = null
  def calcTileIndex(tile:Tile) : Int = 0
  def sortedTiles:List[Tile] = List[Tile]()
  def addHintPenalty { penaltySum += Game.HintPenalty }
  def addMoveablesPenalty { penaltySum += Game.MoveablesPenalty }
  def nextMovePossible = true
  def gameTime = 100000
  def setup:Setup = null
}

class FakeView extends View {
  var started = false
  var stopped = false
  var startedController:Controller = null
  var stoppedController:Controller = null
  override def startView(controller:Controller) { startedController = controller; started = true; }
  override def stopView(controller:Controller) { stoppedController = controller; stopped = true; }
  override def processNotification(sn:SimpleNotification) {}
}

class ControllerSpec extends SpecificationWithJUnit {
  
  val scoreFileName = "controller_scores_test.txt"
  
  def createTestSetup : (FakeGame, FakeView, Controller) = {
    val fakeGame = new FakeGame(scoreFileName)
    val fakeView = new FakeView
    val controller = new Controller(fakeGame) {
      override def closeApplication {}
    }
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
    
    //TODO: check notifications from controller!
    
    "can select tiles" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val clicked1 = new Tile(1, 0, 0, null)
      val clicked2 = new Tile(2, 0, 0, null)
      game.played must beFalse
      game.playedTiles must beNull
      controller.selectTile(clicked1)
      controller.selectTile(clicked2)
      game.played must beTrue
      game.playedTiles.tile1 must be_==(clicked1)
      game.playedTiles.tile2 must be_==(clicked2)
    }
    
    "can start new games" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val setup = new Setup("id", "name", "path")
      game.newGameSetup must beNull
      controller.startNewGame(setup)
      game.newGameSetup must be_==(setup)
    }
    
    "can request hints and moveables" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      game.penaltySum must be_==(0)
      controller.requestHint
      game.penaltySum must be_>=(Game.HintPenalty)
      controller.requestMoveables
      game.penaltySum must be_>=(Game.HintPenalty + Game.MoveablesPenalty)
    }
    
    "can process CloseView notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      view.stopped must beFalse
      controller.detachView(view)
      view.stopped must beTrue
    }
    
    "can process Scramble notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      game.scrambled must beFalse
      controller.scramble
      game.scrambled must beTrue
    }
    
    "can process AddScore notification" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val setup = new Setup("id", "name", "path")
      game.scores.getScores(setup) must have size(0)
      controller.addScore(setup, "name", 12345)
      game.scores.getScores(setup) must have size(1)
      new java.io.File(scoreFileName).delete
    }
  }
}