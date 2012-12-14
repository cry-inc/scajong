package scajong.view.swing

import scajong.model._

import swing._
import scala.swing.event.Event
import javax.swing.ImageIcon

case class SetupSelectedEvent(val setup:Setup) extends Event
case class ScoreSelectedEvent(val setup:Setup) extends Event

abstract class SwingSetupsPanel(setups:List[Setup], caption:String) extends GridPanel(setups.size+1, 1) {
  val setupsPanel = this
  val label = new Label(caption)
  label.peer.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 24))
  contents += label
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