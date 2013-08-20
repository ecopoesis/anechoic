package helpers

import java.net.{MalformedURLException, URL}
import play.api.libs.json._
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.data.validation.ValidationError

object JsonSerialization {

  implicit object UrlReads extends Reads[URL] {
    def reads(json: JsValue) = json match {
      case JsString(s) => {
        try {
          val url = new URL(s)
          JsSuccess(url)
        } catch {
          case e: MalformedURLException => JsError(Seq(JsPath -> Seq(ValidationError("validation error: expected URL"))))
        }
      }
      case _ => JsError(Seq(JsPath -> Seq(ValidationError("validate.error.expected.jsstring"))))
    }
  }

  implicit object UrlWrites extends Writes[URL] {
    def writes(url: URL) = JsString(url.toString)
  }
}
