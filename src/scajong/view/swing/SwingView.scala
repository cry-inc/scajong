package scajong.view.swing

import scajong.model._
import scajong.util._
import scajong.view._
import scajong.controller._
import scala.swing._
import scala.swing.event._
import javax.swing.JFrame._

case class ShowScoresEvent() extends Event
case class StartGameEvent() extends Event

class SwingView(name:String = "") extends Frame with View {  
  
  private var controller:Controller = null
  var fieldPanel:SwingFieldPanel = null
  var scorePanel:SwingScoresPanel = null
  var setupSelectPanel:SwingSetupsPanel = null
  var scoreSelectPanel:SwingSetupsPanel = null

  reactions += {
    case TileClickedEvent(tile) => controller.selectTile(tile)
    case SetupSelectedEvent(setup) => controller.startNewGame(setup)
    case ScoreSelectedEvent(setup) => scorePanel.showScores(setup); selectPanel(scorePanel)
    case AddScoreEvent(setup, name, ms) => controller.addScore(setup, name, ms)
    case StartGameEvent() => selectPanel(setupSelectPanel)
    case ShowScoresEvent() => selectPanel(scoreSelectPanel)
    case WindowClosing(_) => closeView
  }
  
  override def startView(controller:Controller) {
    this.controller = controller
    fieldPanel = new SwingFieldPanel(controller)
    scorePanel = new SwingScoresPanel(controller.scores)
    setupSelectPanel = new SwingSetupsPanel(controller.setups, "Start a new game") {
      def notification(setup:Setup) = new SetupSelectedEvent(setup)
    }
    scoreSelectPanel = new SwingSetupsPanel(controller.setups, "Show scores") {
      def notification(setup:Setup) = new ScoreSelectedEvent(setup)
    }
    
    listenTo(fieldPanel)
    listenTo(scorePanel)
    listenTo(setupSelectPanel)
    listenTo(scoreSelectPanel)
    
    if (controller.tiles.size > 0)
      selectPanel(fieldPanel)
    else
      selectPanel(setupSelectPanel)
  }
  
  override def processNotification(sn:SimpleNotification) {
    sn match {
      case WonNotification(setup, ms, inScoreBoard) => won(setup, ms, inScoreBoard)
      case CreatedGameNotification() => fieldPanel.updateSize; selectPanel(fieldPanel); pack
      // TODO: highlight position in swing view table
      case NewScoreBoardEntryNotification(setup, position) => scorePanel.showScores(setup); selectPanel(scorePanel)
      // Forward all other notifications to the field panel
      case _ => fieldPanel.processNotification(sn)
    }
  }
  
  val swingView = this
  menuBar = new MenuBar {
    contents += new Menu("Game") {
      contents += new MenuItem(Action("Start new Game") {
        swingView.publish(new StartGameEvent)
      })
      contents += new MenuItem(Action("Show Scores") {
        swingView.publish(new ShowScoresEvent)
      })
      contents += new MenuItem(Action("Close") {
        closeView
      })
    }
    contents += new Menu("Cheats") {
      contents += new MenuItem(Action("Show Moveables (+5 sec)") {
        controller.requestMoveables
      })
      contents += new MenuItem(Action("Show Hint (+15 sec)") {
        controller.requestHint
      })
    }
  }
  
  title = "ScaJong"
  if (name.length > 0) title += " " + name

  def closeView {
    controller.detachView(this)
    dispose
  }

  def selectPanel(panel:Panel) {
    visible = false
    minimumSize = new Dimension(640, 480)
    contents = panel
    visible = true
  }

  def won(setup:Setup, ms:Int, inScoreBoard:Boolean) {
    if (inScoreBoard) {
      scorePanel.addScore(setup, ms)
    } else {
      // TODO: Remove dialog
      Dialog.showMessage(null, "Your time: " + (ms / 1000.0) + " seconds", "Missed scoreboard entry")
      scorePanel.showScores(setup)
    }
    selectPanel(scorePanel)
  }
}