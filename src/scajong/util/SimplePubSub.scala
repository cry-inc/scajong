package scajong.util

abstract class SimpleNotification

trait SimplePublisher {
  var subscribers = Set[SimpleSubscriber]()

  def sendNotification(notification:SimpleNotification) {
    subscribers.foreach(subscriber => {
      if (subscriber.notificationProcessor.isDefinedAt(notification)) {
        subscriber.notificationProcessor.apply(notification)
      }
    })
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
  type Reaction = PartialFunction[SimpleNotification, Unit]
  
  var notificationProcessor:Reaction = { case _ => /* Do nothing */ }
}