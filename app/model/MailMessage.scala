package model

import play.api.libs.json.Json

case class MailMessage (
  sender: String,
  subject: String,
  date: String,
  message: String
)

object MailMessage {
  implicit val mmReads = Json.reads[MailMessage]
  implicit val mmWrites = Json.writes[MailMessage]
}

