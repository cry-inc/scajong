package scajong.model

import scajong.util._
import java.io.File

object Setup {
  def CreateSetupsList(setupsDir:String) = {
    val fileArray = new File(setupsDir).listFiles
    val fileNames = fileArray.map(f => f.getPath)
    val filteredFileNames = fileNames.filter(_.endsWith(".txt"))
    filteredFileNames.map(Setup(_)).toList
  }

  def apply(setupFile:String) = {
    val lines = FileUtil.readLines(setupFile)
    new Setup(lines(0), lines(1), setupFile)
  }
}

class Setup(val id:String, val name:String, val path:String) {
  override def toString = {
    "Setup[ID: " + id + "; name: " + name + "; path: " + path + "]"
  }
}