package scajong.controller

import org.specs2.mutable._
import scajong.model._
import scajong.view._
import scajong.model.Setup
import scajong.util.SimpleNotification

class FakeGame(scoreFileName:String) extends Game {
  var played = false
  var playedTiles:TilePair = null
  var selectedSetup:Setup = null
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
  def hint : TilePair = null
  def startNewGame(setup:Setup) { selectedSetup = setup }
  def scramble { scrambled = true }
  def addPenalty(ms:Int) { penaltySum += ms }
  def canMove(tile:Tile) : Boolean = true
  def topmostTile(x:Int, y:Int) : Tile = null
  def findTile(x:Double, y:Double, z:Double) : Tile = null
  def calcTileIndex(x:Int, y:Int, z:Int) : Int = 0
  def calcTileIndex(tile:Tile) : Int = 0
  def sortedTiles:List[Tile] = List[Tile]()
  def addHintPenalty {}
  def addMoveablesPenalty {}
  def nextMovePossible:Boolean = true
  def gameTime:Int = 100000
  def setup:Setup = null
}

class FakeView extends View {
  var started = false
  var stopped = false
  var startedGame:Game = null
  var stoppedGame:Game = null
  override def startView(game:Game) { started = true; startedGame = game }
  override def stopView(game:Game) { stopped = true; stoppedGame = game }
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
      view.startedGame must beNull
      controller.attachView(view)
      view.started must beTrue
      view.startedGame must be_==(game)
    }
    
    "be able to detach a view" in {
      val (game, view, controller) = createTestSetup
      view.stopped must beFalse
      view.stoppedGame must beNull
      controller.detachView(view)
      view.stopped must beTrue
      view.stoppedGame must be_==(game)
    }
    
    "can process TileClicked notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val clicked1 = new Tile(1, 0, 0, null)
      val clicked2 = new Tile(2, 0, 0, null)
      game.selected must beNull
      view.sendNotification(new TileClickedNotification(clicked1))
      game.selected must be_==(clicked1)
      game.played must beFalse
      game.playedTiles must beNull
      view.sendNotification(new TileClickedNotification(clicked2))
      game.played must beTrue
      game.playedTiles.tile1 must be_==(clicked1)
      game.playedTiles.tile2 must be_==(clicked2)
    }
    
    "can process SetupSelected notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val setup = new Setup("id", "name", "path")
      game.selectedSetup must beNull
      view.sendNotification(new SetupSelectedNotification(setup))
      game.selectedSetup must be_==(setup)
    }
    
    "can process Hint and Moveables notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      game.penaltySum must be_==(0)
      view.sendNotification(new RequestHintNotification)
      game.penaltySum must be_==(15000)
      view.sendNotification(new RequestMoveablesNotification)
      game.penaltySum must be_==(20000)
    }
    
    "can process CloseView notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      view.stopped must beFalse
      view.sendNotification(new CloseViewNotification(view))
      view.stopped must beTrue
    }
    
    "can process Scramble notifications" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      game.scrambled must beFalse
      view.sendNotification(new DoScrambleNotification)
      game.scrambled must beTrue
    }
    
    "can process AddScore notification" in {
      val (game, view, controller) = createTestSetup
      controller.attachView(view)
      val setup = new Setup("id", "name", "path")
      game.scores.getScores(setup) must have size(0)
      view.sendNotification(new AddScoreNotification(setup, "name", 12345))
      game.scores.getScores(setup) must have size(1)
      new java.io.File(scoreFileName).delete
    }
  }
}