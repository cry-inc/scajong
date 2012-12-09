package scajong

import scajong.model._
import scajong.view.swing._
import scajong.view.jetty._
import scajong.controller._

object ScaJong {
  def main(args: Array[String]) {
    val game = GameImplementation.create
    val controller = new Controller(game)
    controller.attachView(new SwingView(game, "View 1"))
    controller.attachView(new SwingView(game, "View 2"))
    controller.attachView(new JettyView(game))
  }
}