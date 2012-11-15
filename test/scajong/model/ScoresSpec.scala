package scajong.model

import org.specs2.mutable._

class ScoresSpec extends SpecificationWithJUnit {

  "A ScoreEntry" should {
    val scoreEntry = new ScoreEntry("setupId", "name", 12345)
    
    "have a setupId" in {
      scoreEntry.setupId must be_==("setupId")
    }
    
    "have a name" in {
      scoreEntry.name must be_==("name")
    }
    
    "have a ms" in {
      scoreEntry.ms must be_==(12345)
    }
  }
  
  "A Scores" should {
    //TODO: val scores = new Scores("scores_test.txt", publisher)
  }
}