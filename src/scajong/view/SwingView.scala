package scajong.view

import scajong.model._
import swing._
import swing.event._
import java.io.File
import javax.swing.JFrame._

class TileClickedEvent(val tile:Tile) extends Event
class HintEvent extends Event
class MoveablesEvent extends Event
class ShowScoresEvent extends Event

class SwingView(field:Field, name:String = "") extends Frame {  
  
  val fieldPanel = new SwingFieldPanel(field, name)
  val scorePanel = new SwingScoresPanel(field.scores)
  val setupSelectPanel = new SwingSetupsPanel(field.setups, "Setup")
  val scoreSelectPanel = new SwingSetupsPanel(field.setups, "Score")

  listenTo(field)
  listenTo(fieldPanel)
  listenTo(scorePanel)
  listenTo(setupSelectPanel)
  listenTo(scoreSelectPanel)
  
  reactions += {
    case e: TileClickedEvent => deafTo(this); /*println("view: TileClickedEvent rec.");*/ publish(e); /*println("view: TileClickedEvent forwared!");*/ listenTo(this)
    case e: SetupSelectedEvent => deafTo(this); publish(e); listenTo(this); selectPanel(fieldPanel)
    case e: ScoreSelectedEvent => scorePanel.showScores(e.setupName); selectPanel(scorePanel)
    case e: StartGameEvent => selectPanel(setupSelectPanel)
    case e: ShowScoresEvent => selectPanel(scoreSelectPanel)
    case e: WindowClosing => checkForLastFrame
    case e: WonEvent => scorePanel.addScore(e.setup, e.ms); selectPanel(scorePanel);
    case e: InStartMenuChangedEvent => selectPanel(setupSelectPanel)
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
  
  if (field.inStartMenu)
    selectPanel(setupSelectPanel)
  else
    selectPanel(fieldPanel)
  
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
}