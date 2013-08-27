package helpers

import java.net.{MalformedURLException, URL}
import play.api.libs.json._
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.data.validation.ValidationError
import Json._

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

  implicit def mapWrites[V](implicit fmtv: Writes[V]): OWrites[collection.mutable.Map[String, V]] = OWrites[collection.mutable.Map[String, V]] { ts =>
    JsObject(ts.map { case (k, v) => (k, toJson(v)(fmtv)) }.toList)
  }

  implicit def mapReads[V](implicit fmtv: Reads[V]): Reads[collection.mutable.Map[String, V]] = new Reads[collection.mutable.Map[String, V]] {
    def reads(json: JsValue) = json match {
      case JsObject(m) => {
        // first validates prod separates JsError / JsResult in an Seq[Either( (key, errors, globals), (key, v, jselt) )]
        // the aim is to find all errors prod then to merge them all
        var hasErrors = false

        val r = m.map {
          case (key, value) =>
            fromJson[V](value)(fmtv) match {
              case JsSuccess(v, _) => Right((key, v, value))
              case JsError(e) =>
                hasErrors = true
                Left(e.map { case (p, valerr) => (JsPath \ key) ++ p -> valerr })
            }
        }

        // if errors, tries to merge them into a single JsError
        if (hasErrors) {
          val fulle = r.filter(_.isLeft).map(_.left.get)
            .foldLeft(List[(JsPath, Seq[ValidationError])]())((acc, v) => acc ++ v)
          JsError(fulle)
        } // no error, rebuilds the map
        else JsSuccess(collection.mutable.Map() ++ r.filter(_.isRight).map(_.right.get).map { v => v._1 -> v._2 }.toMap)
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsobject"))))
    }
  }

}
