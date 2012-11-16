package scajong.util

import org.specs2.mutable._
import java.io.File
import java.io.FileOutputStream

class FileUtilSpec extends SpecificationWithJUnit {

  "A FileUtil" should {
    
    "can write text to a file" in {
      val file = "tmpfile0.txt"
      FileUtil.writeText(file, "line1\nline2")
      val f = new File(file)
      f.exists must beTrue
      f.delete
    }
    
    "can read text from a file" in {
      val file = "tmpfile1.txt"
      val input = "line1\nline2"
      FileUtil.writeText(file, input)
      val output = FileUtil.readText(file)
      new File(file).delete
      output must be_==(input)
    }
    
    "can read lines from a file" in {
      val file = "tmpfile2.txt"
      val input = "line1\nline2"
      FileUtil.writeText(file, input)
      val lines = FileUtil.readLines(file)
      new File(file).delete
      lines must have size(2)
      lines(0) must be_==("line1")
      lines(1) must be_==("line2")
    }

    "can read binary data from a file" in {
      val file = "tmpfile3.dat"
      val input = new Array[Byte](2)
      input(0) = 23; input(1) = 42
      val f = new File(file)
      val sw = new FileOutputStream(f)
      sw.write(input)
      sw.close
      val bytes = FileUtil.readBytes(file)
      f.delete
      bytes must have size(2)
      bytes(0) must be_==(23)
      bytes(1) must be_==(42)
    }
    
    "can check if a file exists" in {
      val file = "tmpfile4.txt"
      FileUtil.writeText(file, "content")
      FileUtil.exists(file) must beTrue
      new File(file).delete
      FileUtil.exists(file) must beFalse
    }
  }
}