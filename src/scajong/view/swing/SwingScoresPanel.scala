package scajong.view.swing

import scajong.model._
import swing._
import swing.event._

class AddScoreEvent(val setup:Setup, val name:String, val ms:Int) extends Event

class SwingScoresPanel(scores:Scores) extends GridPanel(1, 1) {

  var currentSetup:Setup = null
  var currentTime = 0

  reactions += {
    case e:EditDone => {
      publish(new AddScoreEvent(currentSetup, e.source.text, currentTime))
      deafTo(e.source)
      showScores(currentSetup)
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
        } else {
          contents += new Label(scoreList(i-1).name)
          contents += new Label((scoreList(i-1).ms / 1000.0).toString)
        }
      }
      visible = true
    }
  }

  def showScores(setup:Setup) {
    visible = false
    val scoreList = scores.getScores(setup)
    rows = Scores.PerSetupEntries + 2
    columns = 3
    prepareGrid(setup.name)
    for (i <- 0 until Scores.PerSetupEntries) {
      contents += new Label((i+1).toString)
      if (i<scoreList.length) {
        contents += new Label(scoreList(i).name)
        contents += new Label((scoreList(i).ms / 1000.0).toString)
      } else {
        contents += new Label()
        contents += new Label()
      }
    }
    visible = true
  }
}