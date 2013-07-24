package model

import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import play.api.libs.json.JsResult
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime

case class Story (
  id: Long,
  title: String,
  url: String,
  score: Int,
  user: Option[User],
  createdAt: DateTime,
  comments: Long
)