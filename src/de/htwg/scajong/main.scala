package de.htwg.scajong

import de.htwg.scajong.model._
import de.htwg.scajong.view.swing._

object ScaJong {
	def main(args: Array[String]) {
		println("This is ScaJong!")
		var field = new Field(new ReverseGenerator("setup.txt", "tiles.txt"))
		var view = new SwingView(field)
	}
}