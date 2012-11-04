package scajong.view

import swing._
import scala.swing.event.Event

class SetupSelectedEvent(val setupFile:String) extends Event
class ScoreSelectedEvent(val setupFile:String) extends Event

class SwingSetupsPanel(setups:Map[String,String], selectType:String) extends BoxPanel(Orientation.Vertical) {
  val setupsPanel = this
  
  for ((setupPath, setupName) <- setups) {
    contents += new Button {
	    setupsPanel.listenTo(this)
	    action = Action(setupName) {
	      // TODO: use generic model?
	      if (selectType == "Score")
	        setupsPanel.publish(new ScoreSelectedEvent(setupPath))
	      else
	        setupsPanel.publish(new SetupSelectedEvent(setupPath))
	    }
	  }
  }
}