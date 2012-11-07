package scajong.model

import scajong.util._

import scala.io.Source
import java.io.File
import java.io.PrintWriter
import util.matching.Regex
import java.io.FileNotFoundException

class ScoreEntry(val setup:String, val name:String, val ms:Int) {
  override def toString = {
    "[Setup:" + setup + ",Name:" + name + ",ms:" + ms + "]"
  }
}

object Scores {
  val perSetupEntries = 10
  val separator = "####"
}

class NewScoreBoardEntryNotification(val setup:String) extends SimpleNotification

class Scores(scoreFile:String, publisher:SimplePublisher) {

  var scores = List[ScoreEntry]()
  
  loadScores
	
	def isInScoreboard(setup:String, ms:Int) = getScores(setup).filter(_.ms < ms).size < Scores.perSetupEntries
	
	implicit val scoreOrdering = Ordering.by((s: ScoreEntry) => s.ms)
	
	def getScores(setup:String) = {
    val sorted = scores.filter(_.setup == setup).sorted
    sorted.take(10)
  }
	
	def countScores(setup:String) = getScores(setup).length
	
	def getScorePosition(setup:String, ms:Int) = {
	  if (!isInScoreboard(setup, ms))
	    -1
    else
    	scores.count(e => e.setup == setup && e.ms < ms)
	}
	
	def addScore(setup:String, name:String, ms:Int) {
	  if (isInScoreboard(setup, ms)) {
	    scores = new ScoreEntry(setup, name, ms) :: scores
	    saveScores
	    publisher.sendNotification(new NewScoreBoardEntryNotification(setup))
	  }
	}
	
	private def saveScores {
	  val lines = for (score <- scores) yield {
	    score.setup + Scores.separator + score.name + Scores.separator + score.ms + "\n"
	  }
	  val writer = new PrintWriter(new File(scoreFile))
    writer.write(lines.mkString)
    writer.close()
	}
	
	private def loadScores {
	  scores = List[ScoreEntry]()
	  try {
		  val source = Source.fromFile(scoreFile)
		  val lines = source.getLines
		  val regex = new Regex("^(.+)" + Scores.separator + "(.+)" + Scores.separator + "(\\d+)$", "setup", "name", "ms")
		  lines.foreach(_ match {
		    case regex(setup, name, ms) => scores = new ScoreEntry(setup, name, ms.toInt) :: scores; 
	      case _ => println("Unknown line!")
		  })
		  source.close
	  } catch {
			case e: FileNotFoundException => // Nothing
	  }
	}
}