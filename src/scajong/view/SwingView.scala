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
class StartGameEvent extends Event

class SwingView(field:Field, name:String = "") extends Frame {  
  
  val fieldPanel = new SwingFieldPanel(field, name)
  val scorePanel = new SwingScoresPanel(new Scores)
  val setupsPanel = new SwingSetupsPanel(List[String]("1", "2", "3"))

  listenTo(fieldPanel)
  listenTo(scorePanel)
  listenTo(setupsPanel)
  
  reactions += {
    case e: TileClickedEvent => deafTo(this); publish(e); listenTo(this)
    case e: WindowClosing => checkForLastFrame
  }
  
  menuBar = new MenuBar{
     contents += new Menu("Game") {
        contents += new MenuItem(Action("Start new Game") {
          publish(new StartGameEvent)
        })
        contents += new MenuItem(Action("Show Scores") {
          publish(new ShowScoresEvent)
        })
        contents += new MenuItem(Action("Close") {
          checkForLastFrame
          dispose
        })
     }
     contents += new Menu("Cheats") {
        contents += new MenuItem(Action("Show Moveables (+5 sec)") {
          publish(new MoveablesEvent)
        })
        contents += new MenuItem(Action("Show Hint (+15 sec)") {
          publish(new HintEvent)
        })
     }
  }
  
  title = "ScaJong"
  if (name.length > 0) title += " " + name
  contents = fieldPanel
  visible = true
  
  def checkForLastFrame {
    val frames = java.awt.Frame.getFrames()
    if (frames.count(!_.isVisible) == 1) System.exit(0)
  }
}