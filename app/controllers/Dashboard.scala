package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.{FeedDao, WidgetDao}
import model.User
import helpers.{Signature, Validation}
import play.api.libs.json._
import play.api.libs.json.Json._

/**
 * widgets controllers
 */
object Dashboard extends Controller with securesocial.core.SecureSocial {
  case class WidgetLocation(widget: Int, column: Int, position: Int)
  implicit val widgetLocationFormat:Format[WidgetLocation] = Json.format[WidgetLocation]

  def getLayout = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        val widgets = WidgetDao.getAll(user.numId)
        for (widget <- widgets) {
          widget.kind match {
            case "feed" => {
              widget.properties += "sig" -> Signature.sign(user.numId.toString + widget.properties.get("url").get)
            }
            case "weather" => {
              widget.properties += "sig" -> Signature.sign(user.numId.toString + widget.properties.get("wunderId").get)
            }
          }
        }
        Ok(Json.toJson(widgets))
      }
      case _ => BadRequest("invalid user object")
    }
  }

  def saveLayout = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        request.body.asJson.map { json =>
          json.validate((__ \ 'widgets).read[List[WidgetLocation]]).map { widgetLocations =>
            if (WidgetDao.saveLayout(user, widgetLocations)) {
              Ok
            } else {
              InternalServerError
            }
          }.recoverTotal { err =>
            BadRequest("could not validate json data")
          }
        }.getOrElse {
          BadRequest("expected json data")
        }
      }
      case _ => BadRequest("invalid user object")
    }
  }
}
