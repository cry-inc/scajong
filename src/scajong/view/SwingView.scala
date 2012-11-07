package scajong.view

import scajong.model._
import scajong.util._

import swing._
import swing.event._
import java.io.File
import javax.swing.JFrame._

class TileClickedNotification(val tile:Tile) extends SimpleNotification
class SetupSelectedNotification(val path:String, val name:String) extends SimpleNotification
class HintNotification extends SimpleNotification
class MoveablesNotification extends SimpleNotification

class ShowScoresEvent extends Event
class StartGameEvent extends Event

class SwingView(field:Field, name:String = "") extends Frame with SimplePublisher with SimpleSubscriber {  
  
  val fieldPanel = new SwingFieldPanel(field, name)
  val scorePanel = new SwingScoresPanel(field.scores)
  val setupSelectPanel = new SwingSetupsPanel(field.setups, "Setup")
  val scoreSelectPanel = new SwingSetupsPanel(field.setups, "Score")

  field.addSubscriber(this)
  listenTo(fieldPanel)
  listenTo(scorePanel)
  listenTo(setupSelectPanel)
  listenTo(scoreSelectPanel)
  
  reactions += {
    case e: TileClickedEvent => sendNotification(new TileClickedNotification(e.tile))
    case e: SetupSelectedEvent => sendNotification(new SetupSelectedNotification(e.setupFile, e.setupName))
    case e: ScoreSelectedEvent => scorePanel.showScores(e.setupName); selectPanel(scorePanel)
    case e: StartGameEvent => selectPanel(setupSelectPanel)
    case e: ShowScoresEvent => selectPanel(scoreSelectPanel)
    case e: WindowClosing => checkForLastFrame
  }
  
  override def processNotifications(sn:SimpleNotification) {
    sn match {
    	case n: WonNotification => won(n.setup, n.ms)
    	case n: CreatedGameNotification => selectPanel(fieldPanel)
      case _ => // Nothing
    }
  }
  
  val swingView = this
  menuBar = new MenuBar{
     contents += new Menu("Game") {
        contents += new MenuItem(Action("Start new Game") {
          swingView.publish(new StartGameEvent)
        })
        contents += new MenuItem(Action("Show Scores") {
          swingView.publish(new ShowScoresEvent)
        })
        contents += new MenuItem(Action("Close") {
          checkForLastFrame
          dispose
        })
     }
     contents += new Menu("Cheats") {
        contents += new MenuItem(Action("Show Moveables (+5 sec)") {
          swingView.publish(new MoveablesEvent)
        })
        contents += new MenuItem(Action("Show Hint (+15 sec)") {
          swingView.publish(new HintEvent)
        })
     }
  }
  
  title = "ScaJong"
  if (name.length > 0) title += " " + name
  
  if (field.tiles.size > 0)
    selectPanel(fieldPanel)
  else
    selectPanel(setupSelectPanel)
  
  def checkForLastFrame {
    val frames = java.awt.Frame.getFrames()
    if (frames.count(!_.isVisible) >= 1) System.exit(0)
    dispose
  }
  
  def selectPanel(panel:Panel) {
    visible = false
    minimumSize = new Dimension(800, 600)
    contents = panel
    visible = true
  }
  
  def won(setup:String, ms:Int) {
    if (field.scores.isInScoreboard(setup, ms)) {
      scorePanel.addScore(setup, ms)
    } else {
      Dialog.showMessage(null, "You time: " + (ms / 1000) + " seconds", "Missed scoreboard entry")
      scorePanel.showScores(setup)
    }
    selectPanel(scorePanel)
  }
}