package scajong.view.swing

import scajong.model._

import swing._
import scala.swing.event.Event
import javax.swing.ImageIcon

class SetupSelectedEvent(val setup:Setup) extends Event
class ScoreSelectedEvent(val setup:Setup) extends Event

class SwingSetupsPanel(setups:List[Setup], selectType:String) extends GridPanel(setups.size, 1) {
  val setupsPanel = this
  
  for (setup <- setups) {
    contents += new Button {
      setupsPanel.listenTo(this)
      action = Action(setup.name) {
	      // TODO: use generic model?
	      if (selectType == "Score")
	        setupsPanel.publish(new ScoreSelectedEvent(setup))
	      else if (selectType == "Setup")
	        setupsPanel.publish(new SetupSelectedEvent(setup))
	    }
      icon = new ImageIcon(setup.path.replace(".txt", ".png"))
      iconTextGap = 20
      focusPainted = false
	  }
  }
}