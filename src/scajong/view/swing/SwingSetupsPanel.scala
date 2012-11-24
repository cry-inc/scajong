package scajong.view.swing

import scajong.model._

import swing._
import scala.swing.event.Event
import javax.swing.ImageIcon

class SetupSelectedEvent(val setup:Setup) extends Event
class ScoreSelectedEvent(val setup:Setup) extends Event

abstract class SwingSetupsPanel(setups:List[Setup]) extends GridPanel(setups.size, 1) {
  val setupsPanel = this
  for (setup <- setups) {
    contents += new Button {
      setupsPanel.listenTo(this)
      action = Action(setup.name) {
	      setupsPanel.publish(notification(setup))
	    }
      icon = new ImageIcon(setup.path.replace(".txt", ".png"))
      iconTextGap = 20
      focusPainted = false
	  }
  }
  
  def notification(setup:Setup) : Event
}