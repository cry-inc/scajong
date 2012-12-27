package scajong.util

import org.specs2.mutable._

class SimplePubSubSpec extends SpecificationWithJUnit {

  case object MyNotification extends SimpleNotification
  
  "A SimpleSubscriber" should {
    "process notifications" in {
      val subscriber = new SimpleSubscriber {
        var notificated = false
        notificationProcessor = {
          case _ => notificated = true
        }
      }
      
      subscriber.notificated must beFalse
      subscriber.notificationProcessor.apply(MyNotification)
      subscriber.notificated must beTrue
    }
  }
  
  "A SimplePublisher" should {    
    
    "can attach and remove subscribers" in {
      val publisher = new SimplePublisher {}
      val subscriber = new SimpleSubscriber {}
      
      publisher.addSubscriber(subscriber)
      publisher.subscribers must have size(1)
      publisher.remSubscriber(subscriber)
      publisher.subscribers must have size(0)
    }
    
    "can notifiy subscribers" in {
      val publisher = new SimplePublisher {}
      val subscriber = new SimpleSubscriber {
        var notificated = false
        notificationProcessor = {
          case _ => notificated = true
        }
      }
      
      subscriber.notificated must beFalse
      publisher.addSubscriber(subscriber)
      publisher.sendNotification(MyNotification)
      subscriber.notificated must beTrue
      publisher.remSubscriber(subscriber)
    }
  }
}