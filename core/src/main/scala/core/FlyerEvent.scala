package core

final case class FlyerEvent(
  user_id: String,
  event_type: EventType,
  flyer_id: Option[String],
  merchant_id: Option[String]
)
