package scajong.util

import scala.io.Source
import java.io.File
import java.io.FileInputStream
import java.io.PrintWriter

object FileUtil {
	
  def readText(file:String) : String = {
	  val source = Source.fromFile(file)
		val lines = source.mkString
		source.close
	  lines
	}
  
	def writeText(file:String, content:String) {
	  val writer = new PrintWriter(new File(file))
    writer.write(content)
    writer.close
	}
	
	def readLines(file:String) : List[String] = {
	  val source = Source.fromFile(file)
	  var lines = List[String]()
		source.getLines.foreach(l => lines = l :: lines)
		source.close
		lines
	}
	
	def readBytes(file:String) : Array[Byte] = {
		val is = new FileInputStream(file)
		val cnt = is.available
		val bytes = Array.ofDim[Byte](cnt)
		is.read(bytes)
		is.close
		bytes
	}
	
	def exists(file:String) : Boolean = {
	  val f = new File(file)
	  f.exists
	}
}