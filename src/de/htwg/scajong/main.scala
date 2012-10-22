package de.htwg.scajong

import de.htwg.scajong.model._

object ScaJong {
	def main(args: Array[String]) {
		println("This is ScaJong!")
		var field = new Field(new ReverseGenerator("setup.txt", "tiles.txt"))
	}
}