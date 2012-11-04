package scajong

import scajong.model._
import scajong.view._
import scajong.controller._

object ScaJong {
	def main(args: Array[String]) {
		var field = new Field("setups/", "tiles.txt", new ReverseGenerator)
		var controller = new SwingController(field)
		controller.attachView(new SwingView(field, "View 1"))
		controller.attachView(new SwingView(field, "View 2"))
	}
}