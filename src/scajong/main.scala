package scajong

import scajong.model._
import scajong.view._
import scajong.controller._

object ScaJong {
	def main(args: Array[String]) {
		println("This is ScaJong!")
		var field = new Field(new ReverseGenerator("setup.txt", "tiles.txt"))
		var controller = new SwingController(field)
		controller.attachView(new SwingView(field, "View 1"))
		controller.attachView(new SwingView(field, "View 2"))
	}
}