package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.{UserDao, FeedDao, WidgetDao}
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

  case class DeleteWidget(id: Long, sig: String)

  val deleteWidgetData = Form(
    mapping(
      "id" -> longNumber,
      "sig" -> nonEmptyText(maxLength = 28)
    )(DeleteWidget.apply)(DeleteWidget.unapply)
  )

  def getLayout = UserAwareAction { implicit request =>
    val widgets = request.user match {
      case user: Some[User] => {
        WidgetDao.getAll(user.get.numId)
      }
      case None => {
        WidgetDao.getAll(UserDao.getByUsername("system").get.numId)
      }
    }

    for (widget <- widgets) {
      widget.kind match {
        case "feed" => {
          widget.properties += "sig" -> Signature.sign(widget.properties.get("url").get)
        }
        case "stock" => {
          widget.properties += "sig" -> Signature.sign(widget.properties.get("symbol").get)
        }
        case "weather" => {
          widget.properties += "sig" -> Signature.sign(widget.properties.get("wunderId").get)
        }
        case _ => {}
      }
      widget.properties += "delsig" -> Signature.sign(widget.id.toString)
    }

    Ok(Json.toJson(widgets))
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

  def deleteWidget = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        deleteWidgetData.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            if (Signature.check(post.sig, post.id.toString)) {
              if (WidgetDao.delete(post.id)) {
                Ok
              } else {
                InternalServerError
              }
            } else {
              BadRequest("invalid signature")
            }
          }
        )
      }
      case _ => BadRequest("invalid user object")
    }
  }
}
