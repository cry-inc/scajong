package scajong.util

class SimpleNotification

trait SimplePublisher {
  private var subscribers = Set[SimpleSubscriber]()
  
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
  def processNotifications(notification:SimpleNotification)
}