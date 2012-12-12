package scajong

import scajong.model._
import scajong.view.swing._
import scajong.view.jetty._
import scajong.view.tui._
import scajong.controller._

object Scajong {
  
  val game = GameImplementation.create()
  val controller = new Controller(game)
  
  def main(args: Array[String]) {
    controller.attachView(new SwingView(game, "View 1"))
    controller.attachView(new SwingView(game, "View 2"))
    controller.attachView(new JettyView(game))
    controller.attachView(new TextUI(game))
  }
}