package scajong.view

import scajong.model._
import swing._
import swing.event._

object HelloWorld extends SimpleSwingApplication {
	def top = new MainFrame {
		val scores = new Scores("scores.txt")
		val scoresPanel = new SwingScoresPanel(scores)
	  title = "ScaJong Scores"
	  contents = scoresPanel
	  size = new Dimension(640, 480)
	  visible = true
	  scoresPanel.addScore("setup", 110787)
	}
}

class SwingScoresPanel(val scores:Scores) extends GridPanel(1, 1) {

  var currentSetup = ""
  var currentTime = 0
  
  reactions += {
    case e:EditDone => {
      scores.addScore(currentSetup, e.source.text, currentTime)
      deafTo(e.source)
      showScores(currentSetup)
    }
  }
  
  def prepareGrid(setup:String, scoreList:List[ScoreEntry]) {
    contents.clear
    addCaptionsToGrid(setup)
  }
  
  def addCaptionsToGrid(setup:String) {
    contents += new Label("------")
    contents += new Label(setup)
    contents += new Label("------")
    contents += new Label("Position")
    contents += new Label("Name")
    contents += new Label("Seconds")
  }
  
  def addScore(setup:String, ms:Int) {
    if (scores.isInScoreboard(setup, ms)) {
	    visible = false
      val scoreList = scores.getScores(setup)
	    rows = scoreList.length + 3
	    columns = 3
	    prepareGrid(setup, scoreList)
	    val pos = scores.getScorePosition(setup, ms)
	    val textField = new TextField("Anonymous")
	    currentSetup = setup
	    currentTime = ms
	    textField.selectAll
	    listenTo(textField)
		  for (i <- 0 until pos) {
		    contents += new Label((i+1).toString)
	    	contents += new Label(scoreList(i).name)
		    contents += new Label((scoreList(i).ms / 1000.0).toString)
		  }
	    contents += new Label((pos+1).toString)
	    contents += textField
	    contents += new Label((ms / 1000.0).toString)
	    for (i <- pos until scoreList.length-1) {
		    contents += new Label((i+2).toString)
	    	contents += new Label(scoreList(i).name)
		    contents += new Label((scoreList(i).ms / 1000.0).toString)
		  }
	    visible = true
    } else showScores(setup)
  }
  
  def showScores(setup:String) {
    visible = false
    val scoreList = scores.getScores(setup)
    rows = scoreList.length + 2
    columns = 3
    prepareGrid(setup, scoreList)
	  for (i <- 0 until scoreList.length) {
	    contents += new Label((i+1).toString)
	    contents += new Label(scoreList(i).name)
	    contents += new Label((scoreList(i).ms / 1000.0).toString)
	  }
    visible = true
  }
}