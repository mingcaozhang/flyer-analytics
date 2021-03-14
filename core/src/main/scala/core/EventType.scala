package core

import enumeratum._

import scala.collection.immutable

sealed trait EventType extends EnumEntry

object EventType extends Enum[EventType] with CirceEnum[EventType] {
  val values: immutable.IndexedSeq[EventType] = findValues

  case object flyer_open extends EventType
  case object item_open extends EventType
  case object list_flyers extends EventType
  case object shopping_list_open extends EventType
  case object favorite extends EventType
}
