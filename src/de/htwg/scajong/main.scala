package de.htwg.scajong

import de.htwg.scajong.model._
import de.htwg.scajong.view.swing._
import de.htwg.scajong.controller.SwingController

object ScaJong {
	def main(args: Array[String]) {
		println("This is ScaJong!")
		var field = new Field(new ReverseGenerator("setup.txt", "tiles.txt"))
		var view1 = new SwingView(field, "1x")
		var view2 = new SwingView(field, "2x")
		var controller = new SwingController(field)
		controller.attachView(view1)
		controller.attachView(view2)
	}
}