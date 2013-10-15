package model

import play.api.libs.json.Json

case class Error (
  error: String
)

object Error {
  implicit val errorReads = Json.reads[Error]
  implicit val errorWrites = Json.writes[Error]
}
