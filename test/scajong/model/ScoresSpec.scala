package scajong.model

import scajong.util._
import org.specs2.mutable._

class ScoresSpec extends SpecificationWithJUnit {

  "A ScoreEntry" should {
    val scoreEntry = new ScoreEntry("setupId", "Name", 12345)
    
    "have a setupId" in {
      scoreEntry.setupId must be_==("setupId")
    }
    
    "have a name" in {
      scoreEntry.name must be_==("Name")
    }
    
    "have a ms" in {
      scoreEntry.ms must be_==(12345)
    }
    
    "be a string" in {
      scoreEntry.toString must be_==("[Setup:setupId,Name:Name,ms:12345]")
    }
  }
  
  "A Scores instance" should {
    val setup1 = new Setup("id1", "Name 1", "path/to/setup1.txt")
    val setup2 = new Setup("id2", "Name 2", "path/to/setup2.txt")
    val scoreFileName = "scores_test.txt"
    new java.io.File(scoreFileName).delete
    val scores = new Scores(scoreFileName)
    scores.addScore(setup1, "Otto", 12345)
    scores.addScore(setup1, "Willi", 18345)
    for (i <- 1 to Scores.PerSetupEntries) scores.addScore(setup2, "Heiner", 10000 * i)
    
    "can check if a time will be in the scores" in {
      scores.isInScoreboard(setup2, 10000) must beTrue
      scores.isInScoreboard(setup2, Scores.PerSetupEntries * 10000 + 1) must beFalse
    }
    
    "will return sorted scores" in {
      val list = scores.getScores(setup1)
      list(0).name must be_==("Otto")
      list(1).name must be_==("Willi")
    }
    
    "can calculate the position for a time" in {
      scores.getScorePosition(setup1, 10000) must be_==(0)
      scores.getScorePosition(setup1, 13000) must be_==(1)
      scores.getScorePosition(setup2, 200000) must be_==(-1)
    }
    
    "can add and save new scores" in {
      scores.addScore(setup1, "Michi", 33000) must be_==(2)
      scores.getScores(setup1) must have size(3)
      scores.addScore(setup2, "Last", Scores.PerSetupEntries * 10000 + 1) must be_==(-1)
      scores.getScores(setup2) must have size(Scores.PerSetupEntries)
      new java.io.File(scoreFileName).delete
    }
  }
}