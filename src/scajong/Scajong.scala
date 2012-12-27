package scajong

import scajong.model._
import scajong.view.swing._
import scajong.view.jetty._
import scajong.view.tui._
import scajong.controller._

// TODO: case objects statt classes wenn keine params
// TODO: zipped fuer verschachtelte schleifen einbinden
// TODO: Jetty und andere externe updaten
// TODO: Scala 2.10 testen
// TODO: Jetty HTML-Files mergen

object Scajong {
  
  val game = GameImplementation.create()
  val controller = new Controller(game)
  
  def main(args: Array[String]) {
    controller.attachView(new SwingView)
    //controller.attachView(new SwingView("Scajong View 2"))
    controller.attachView(new JettyView)
    
    /*
    val tui = new TextUI
    controller.attachView(tui)
    while (tui.readCommand) {}
    */
  }
}