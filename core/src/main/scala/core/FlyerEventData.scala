package core

final case class FlyerEventData(
  user_id: String,
  event_type: EventType,
  flyer_id: Option[String],
  merchant_id: Option[String],
  timestamp: Long
)

object FlyerEventData {
  def apply(flyerEvent: FlyerEvent): FlyerEventData =
    FlyerEventData(
      flyerEvent.user_id,
      flyerEvent.event_type,
      flyerEvent.flyer_id,
      flyerEvent.merchant_id,
      System.currentTimeMillis()
    )
}
