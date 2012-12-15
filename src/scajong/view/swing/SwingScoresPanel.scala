package scajong.view.swing

import scajong.model._
import swing._
import swing.event._

case class AddScoreEvent(val setup:Setup, val name:String, val ms:Int) extends Event

class SwingScoresPanel(scores:Scores) extends GridPanel(1, 1) {

  var currentSetup:Setup = null
  var currentTime = 0

  reactions += {
    case e:EditDone => {
      publish(new AddScoreEvent(currentSetup, e.source.text, currentTime))
      deafTo(e.source)
    }
  }

  def prepareGrid(setupName:String) {
    contents.clear
    addCaptionsToGrid(setupName)
  }

  def addCaptionsToGrid(setupName:String) {
    contents += new Label("------")
    contents += new Label(setupName)
    contents += new Label("------")
    contents += new Label("Position")
    contents += new Label("Name")
    contents += new Label("Seconds")
  }

  def addScore(setup:Setup, ms:Int) {
    if (!scores.isInScoreboard(setup, ms)) {
      showScores(setup)
    } else {
      visible = false
      prepareGrid(setup.name)
      val pos = scores.getScorePosition(setup, ms)
      val textField = new TextField("Anonymous")
      currentSetup = setup
      currentTime = ms
      listenTo(textField)
      val scoreList = scores.getScores(setup)
      rows = Scores.PerSetupEntries + 2
      columns = 3
      for (i <- 0 until Scores.PerSetupEntries) {
        contents += new Label((i+1).toString)
        if (i < pos) {
          contents += new Label(scoreList(i).name)
          contents += new Label((scoreList(i).ms / 1000.0).toString)
        } else if (i == pos) {
          contents += textField
          contents += new Label((ms / 1000.0).toString)
        } else if (scoreList.size >= i) {
          contents += new Label(scoreList(i-1).name)
          contents += new Label((scoreList(i-1).ms / 1000.0).toString)
        } else {
          contents += new Label()
          contents += new Label()
        }
      }
      visible = true
    }
  }

  def showScores(setup:Setup, position:Int = -1, message:String = "") {
    visible = false
    val scoreList = scores.getScores(setup)
    rows = Scores.PerSetupEntries + 3
    columns = 3
    prepareGrid(setup.name)
    val highlightColor = new Color(255, 0, 0) 
    for (i <- 0 until Scores.PerSetupEntries) {
      val posLabel = new Label((i+1).toString)
      contents += posLabel
      if (i<scoreList.length) {
        val nameLabel = new Label(scoreList(i).name)
        val timeLabel = new Label((scoreList(i).ms / 1000.0).toString)
        if (i == position) {
          nameLabel.peer.setForeground(highlightColor);
          timeLabel.peer.setForeground(highlightColor);
          posLabel.peer.setForeground(highlightColor);
        }
        contents += nameLabel
        contents += timeLabel
      } else {
        contents += new Label()
        contents += new Label()
      }
    }
    val messageLabel = new Label(message)
    messageLabel.peer.setForeground(highlightColor)
    contents += new Label()
    contents += messageLabel
    contents += new Label()
    visible = true
  }
}