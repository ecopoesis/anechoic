package model

import org.joda.time.DateTime
import play.api.libs.json.Json
import helpers.JsonSerialization._

case class Widget(
  id: Long,
  userId: Long,
  kind: String,
  column: Option[Int],
  position: Option[Int],
  createdAt: DateTime,
  properties: collection.mutable.Map[String, String]
)

object Widget {
  implicit val widgetReads = Json.reads[Widget]
  implicit val widgetWrites = Json.writes[Widget]
}