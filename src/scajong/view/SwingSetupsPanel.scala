package scajong.view

import swing._
import scala.swing.event.Event
import java.awt.Dialog
import javax.swing.ImageIcon

class SetupSelectedEvent(val setupFile:String, val setupName:String) extends Event
class ScoreSelectedEvent(val setupFile:String, val setupName:String) extends Event

class SwingSetupsPanel(setups:Map[String,String], selectType:String) extends GridPanel(setups.size, 1) {
  val setupsPanel = this
  
  for ((setupPath, setupName) <- setups) {
    contents += new Button {
      setupsPanel.listenTo(this)
      action = Action(setupName) {
	      // TODO: use generic model?
	      if (selectType == "Score")
	        setupsPanel.publish(new ScoreSelectedEvent(setupPath, setupName))
	      else if (selectType == "Setup")
	        setupsPanel.publish(new SetupSelectedEvent(setupPath, setupName))
	    }
      icon = new ImageIcon(setupPath.replace(".txt", ".png"))
      iconTextGap = 20
      focusPainted = false
	  }
  }
}