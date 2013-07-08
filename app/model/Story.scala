package model

import play.api.libs.json._
import play.api.libs.json.util._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import play.api.libs.json.JsResult
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Story (
  id: Long,
  title: String,
  url: String,
  score: Int
)

object Story {
  implicit val storyReads:Reads[Story] = (
    (__ \ "id").read[Long] ~
    (__ \ "title").read[String] ~
    (__ \ "url").read[String] ~
    (__ \ "score").read[Int]
  )(Story.apply _)

  implicit val storyWrites:Writes[Story] = (
    (__ \ "id").write[Long] ~
    (__ \ "title").write[String] ~
    (__ \ "url").write[String] ~
    (__ \ "score").write[Int]
  )(unlift(Story.unapply))
}
