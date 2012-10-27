package scajong.model

import java.io.File
import java.io.PrintWriter
import util.matching.Regex

class ScoreEntry(val setup:String, val name:String, val ms:Int)

object Scores {
  val perSetupEntries = 10
  val separator = "####"
}

class Scores {
	var scores = List[ScoreEntry]()
	
	def isInScoreboard(setup:String, ms:Int) = getScores(setup).filter(_.ms < ms).size < Scores.perSetupEntries
	
	def getScores(setup:String) = scores.filter(_.setup == setup)
	
	def addScore(setup:String, name:String, ms:Int) {
	  if (isInScoreboard(setup, ms)) {
	    scores = new ScoreEntry(setup, name, ms) :: scores
	  }
	}
	
	def saveScores(file:String) {
	  val lines = for (score <- scores) yield {
	    score.setup + Scores.separator + score.name + Scores.separator + score.ms + "\n"
	  }
	  val writer = new PrintWriter(new File("test.txt" ))
    writer.write(lines.mkString)
    writer.close()
	}
	
	def loadScores(file:String) {
	  scores = List[ScoreEntry]()
	  val text = io.Source.fromFile(file).mkString
	  val regex = new Regex("^(.+)" + Scores.separator + "(.+)" + Scores.separator + "(\\d+)$", "setup", "name", "ms")
    regex.findAllIn(text).foreach(_ match {
      case regex(setup, name, ms) => scores = new ScoreEntry(setup, name, ms.toInt) :: scores
      case _ => println("Unknown line!")
	  })
	}
}