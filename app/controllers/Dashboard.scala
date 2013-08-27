package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.{WidgetDao}
import model.User
import helpers.{Signature, Validation}
import play.api.libs.json._
import play.api.libs.json.Json._

/**
 * dashboard controllers
 */
object Dashboard extends Controller with securesocial.core.SecureSocial {
  case class AddFeedPost(max: Int, url: String)
  case class AddWeatherPost(city: String, wunderId: String)
  case class WidgetLocation(widget: Int, column: Int, position: Int)

  implicit val widgetLocationFormat:Format[WidgetLocation] = Json.format[WidgetLocation]

  val addFeedPost = Form(
    mapping(
      "max" -> number,
      "url" -> nonEmptyText(maxLength = 2000)
        .verifying("must be an URL", url => Validation.url(url))
    )(AddFeedPost.apply)(AddFeedPost.unapply)
  )

  val addWeatherPost = Form(
    mapping(
      "city" -> nonEmptyText(maxLength = 2000),
      "wunder_id" -> nonEmptyText(maxLength = 2000)
    )(AddWeatherPost.apply)(AddWeatherPost.unapply)
  )

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

  def addFeed = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        addFeedPost.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            if (WidgetDao.addFeed(user, post.url, post.max)) {
              Ok
            } else {
              InternalServerError
            }
          }
        )
      }
      case _ => BadRequest("invalid user object")
    }
  }

  def addWeather = SecuredAction { implicit  request =>
    request.user match {
      case user: User => {
        addWeatherPost.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            if (WidgetDao.addWeather(user, post.city, post.wunderId)) {
              Ok
            } else {
              InternalServerError
            }
          }
        )
      }
      case _ => BadRequest("invalid user object")
    }
  }
}
