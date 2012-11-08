package scajong.view.swing

import scala.swing.Publisher
import scala.swing.event.Event
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Timer

// Origin: http://stackoverflow.com/questions/6825729/how-to-write-a-scala-wrapper-for-javax-swing-timer

case class TimerEvent(val name:String) extends Event

class ScalaTimer(delay:Int, name:String) extends Timer(delay, null) with Publisher {

  def this(delay:Int, name:String, action:(()=>Unit)) = {
    this(delay, name)
    reactions += {
      case TimerEvent(_) => action()
    }
  }

  addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) = publish(TimerEvent(name))
  })
}