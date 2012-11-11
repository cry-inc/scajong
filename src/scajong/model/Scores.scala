package scajong.model

import scajong.util._
import scajong.util.FileUtil

import util.matching.Regex
import java.io.FileNotFoundException

class ScoreEntry(val setupId:String, val name:String, val ms:Int) {
  override def toString = {
    "[Setup:" + setupId + ",Name:" + name + ",ms:" + ms + "]"
  }
}

object Scores {
  val perSetupEntries = 10
  val separator = "####"
}

class NewScoreBoardEntryNotification(val setup:Setup) extends SimpleNotification

class Scores(scoreFile:String, publisher:SimplePublisher) {

  var scores = List[ScoreEntry]()
  
  loadScores
	
	def isInScoreboard(setup:Setup, ms:Int) = getScores(setup).filter(_.ms < ms).size < Scores.perSetupEntries
	
	implicit val scoreOrdering = Ordering.by((s: ScoreEntry) => s.ms)
	
	def getScores(setup:Setup) = {
    val sorted = scores.filter(_.setupId == setup.id).sorted
    sorted.take(10)
  }
	
	def getScorePosition(setup:Setup, ms:Int) = {
	  if (!isInScoreboard(setup, ms))
	    -1
    else
    	scores.count(e => e.setupId == setup.id && e.ms < ms)
	}
	
	def addScore(setup:Setup, name:String, ms:Int) {
	  if (isInScoreboard(setup, ms)) {
	    scores = new ScoreEntry(setup.id, name, ms) :: scores
	    saveScores
	    publisher.sendNotification(new NewScoreBoardEntryNotification(setup))
	  }
	}
	
	private def saveScores {
	  val lines = for (score <- scores) yield {
	    score.setupId + Scores.separator + score.name + Scores.separator + score.ms + "\n"
	  }
	  FileUtil.writeText(scoreFile, lines.mkString)
	}
	
	private def loadScores {
	  scores = List[ScoreEntry]()
	  try {
		  val lines = FileUtil.readLines(scoreFile)
		  val regex = new Regex("^(.+)" + Scores.separator + "(.+)" + Scores.separator + "(\\d+)$", "setupId", "name", "ms")
		  lines.foreach(_ match {
		    case regex(setupId, name, ms) => scores = new ScoreEntry(setupId, name, ms.toInt) :: scores; 
	      case _ => // Ignore
		  })
	  } catch {
			case e: FileNotFoundException => // Nothing
	  }
	}
}