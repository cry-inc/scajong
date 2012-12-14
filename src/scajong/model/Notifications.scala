package scajong.model

import scajong.util.SimpleNotification

// Notifications towards the views
case class StartHintNotification(hint:TilePair) extends SimpleNotification
case class StopHintNotification() extends SimpleNotification
case class StartMoveablesNotification() extends SimpleNotification
case class StopMoveablesNotification() extends SimpleNotification