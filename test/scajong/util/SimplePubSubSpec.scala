package scajong.util

import org.specs2.mutable._

class SimplePubSubSpec extends SpecificationWithJUnit {

  "A SimpleSubscriber" should {

    "can process notifications" in {
    	val subscriber = new SimpleSubscriber {
    		var notificated = false
    				def processNotifications(notification:SimpleNotification) {
    			notificated = true
    		}
    	}
    	
      subscriber.notificated must beFalse
      subscriber.processNotifications(new SimpleNotification)
      subscriber.notificated must beTrue
    }
  }
  
  "A SimplePublisher" should {    
    
    "can attach and remove subscribers" in {
	    val publisher = new SimplePublisher {}
	    val subscriber = new SimpleSubscriber {
	      def processNotifications(notification:SimpleNotification) {}
	    }
	    
      publisher.addSubscriber(subscriber)
      publisher.subscribers must have size(1)
      publisher.remSubscriber(subscriber)
      publisher.subscribers must have size(0)
    }
    
    "can notifiy subscribers" in {
    	val publisher = new SimplePublisher {}
    	val subscriber = new SimpleSubscriber {
    		var notificated = false
    				def processNotifications(notification:SimpleNotification) {
    			notificated = true
    		}
    	}
    	
      subscriber.notificated must beFalse
      publisher.addSubscriber(subscriber)
      publisher.sendNotification(new SimpleNotification)
      subscriber.notificated must beTrue
      publisher.remSubscriber(subscriber)
    }
  }
}