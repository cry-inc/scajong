package scajong

import scajong.model._
import scajong.view.swing._
import scajong.controller._

object ScaJong {
	def main(args: Array[String]) {
		val field = new Field("setups/", "tiles.txt", new ReverseGenerator)
		val controller = new SwingController(field)
		controller.attachView(new SwingView(field, "View 1"))
		controller.attachView(new SwingView(field, "View 2"))
	}
}