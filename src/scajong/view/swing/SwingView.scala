package scajong.view.swing

import scajong.model._
import scajong.util._
import scajong.view._
import scala.swing._
import scala.swing.event._
import javax.swing.JFrame._

case class ShowScoresEvent() extends Event
case class StartGameEvent() extends Event

// TODO: remove game contructor argument
class SwingView(game:Game, name:String = "") extends Frame with View {  
  
  val fieldPanel = new SwingFieldPanel(game, name)
  val scorePanel = new SwingScoresPanel(game.scores)
  val setupSelectPanel = new SwingSetupsPanel(game.setups, "Start a new game") {
    def notification(setup:Setup) = new SetupSelectedEvent(setup)
  }
  val scoreSelectPanel = new SwingSetupsPanel(game.setups, "Show scores") {
    def notification(setup:Setup) = new ScoreSelectedEvent(setup)
  }

  listenTo(fieldPanel)
  listenTo(scorePanel)
  listenTo(setupSelectPanel)
  listenTo(scoreSelectPanel)
  
  reactions += {
    case TileClickedEvent(tile) => sendNotification(new TileClickedNotification(tile))
    case SetupSelectedEvent(setup) => sendNotification(new SetupSelectedNotification(setup))
    case ScoreSelectedEvent(setup) => scorePanel.showScores(setup); selectPanel(scorePanel)
    case AddScoreEvent(setup, name, ms) => sendNotification(new AddScoreNotification(setup, name, ms))
    case StartGameEvent() => selectPanel(setupSelectPanel)
    case ShowScoresEvent() => selectPanel(scoreSelectPanel)
    case WindowClosing(_) => closeView
  }
  
  override def processNotification(sn:SimpleNotification) {
    sn match {
      case WonNotification(setup, ms, inScoreBoard) => won(setup, ms)
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
        sendNotification(new RequestMoveablesNotification)
      })
      contents += new MenuItem(Action("Show Hint (+15 sec)") {
        sendNotification(new RequestHintNotification)
      })
    }
  }
  
  title = "ScaJong"
  if (name.length > 0) title += " " + name
  
  if (game.tiles.size > 0)
    selectPanel(fieldPanel)
  else
    selectPanel(setupSelectPanel)
  
  def closeView {
    sendNotification(new CloseViewNotification(this))
    dispose
  }

  def selectPanel(panel:Panel) {
    visible = false
    minimumSize = new Dimension(640, 480)
    contents = panel
    visible = true
  }

  def won(setup:Setup, ms:Int) {
    if (game.scores.isInScoreboard(setup, ms)) {
      scorePanel.addScore(setup, ms)
    } else {
      // TODO: Remove dialog
      Dialog.showMessage(null, "Your time: " + (ms / 1000.0) + " seconds", "Missed scoreboard entry")
      scorePanel.showScores(setup)
    }
    selectPanel(scorePanel)
  }
}