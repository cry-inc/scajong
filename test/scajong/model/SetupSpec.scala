package scajong.model

import org.specs2.mutable._

class SetupSpec extends SpecificationWithJUnit {

  "A Setup" should {
    val setup = new Setup("id", "N&ame", "C:\\path/setup.txt")
    
    "have an id" in {
      setup.id must be_==("id")
    }
    
    "have a name" in {
      setup.name must be_==("N&ame")
    }
    
    "have a path" in {
      setup.path must be_==("C:\\path/setup.txt")
    }
    
    "have a second contructor" in {
      val secondSetup = Setup("setups/camel.txt")
      secondSetup.id must be_==("camel")
      secondSetup.name must be_==("Camel")
      secondSetup.path must be_==("setups/camel.txt")
    }
    
    "have a setup list from dir function" in {
      val setups = Setup.CreateSetupsList("setups/")
      setups.size must be_>=(1)
      setups.filter(_.id == "camel") must have size 1
      setups.filter(_.name == "Camel") must have size 1
      setups.filter(s => s.path == "setups/camel.txt" || s.path == "setups\\camel.txt") must have size 1
    }
  }
}