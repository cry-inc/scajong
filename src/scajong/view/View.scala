package scajong.view

import scajong.util._
import scajong.model._
import scajong.controller._

trait View extends SimplePublisher with SimpleSubscriber {
  def startView(controller:Controller) {}
  def stopView(controller:Controller) {}
  def autoClose = false
}