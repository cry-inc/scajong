package scajong.util

class SimpleNotification

trait SimplePublisher {
  var subscribers = Set[SimpleSubscriber]()

  def sendNotification(notification:SimpleNotification) {
    subscribers.foreach(_.processNotifications(notification))
  }

  def addSubscriber(subscriber:SimpleSubscriber) {
    if (!subscribers.contains(subscriber))
      subscribers += subscriber
  }

  def remSubscriber(subscriber:SimpleSubscriber) {
    if (subscribers.contains(subscriber))
      subscribers -= subscriber
  }
}

trait SimpleSubscriber {
  // TODO: remove s
  def processNotifications(notification:SimpleNotification)
}