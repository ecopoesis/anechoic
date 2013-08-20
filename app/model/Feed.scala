package model

import java.net.URL
import org.joda.time.DateTime
import play.api.libs.json._
import helpers.JsonSerialization._

case class Item (
  title: String,
  description: String,
  link: URL,
  date: DateTime,
  author: String
)

case class Feed (
  title: String,
  description: String,
  link: URL,
  date: DateTime,
  items: Seq[Item]
)

object Item {
  implicit val itemReads = Json.reads[Item]
  implicit val itemWrites = Json.writes[Item]
}

object Feed {
  implicit val feedReads = Json.reads[Feed]
  implicit val feedWrites = Json.writes[Feed]
}
