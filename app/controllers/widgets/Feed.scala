package controllers.widgets

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import helpers.{Signature, Validation}
import model.User
import dao.{WidgetDao, FeedDao}
import play.api.libs.json.Json

object Feed extends Controller with securesocial.core.SecureSocial {
  case class Add(max: Int, url: String)
  case class Read(sig: String, url: String)

  val addData = Form(
    mapping(
      "max" -> number,
      "url" -> nonEmptyText(maxLength = 2000)
        .verifying("must be an URL", url => Validation.url(url))
    )(Add.apply)(Add.unapply)
  )

  val readData = Form(
    mapping(
      "sig" -> nonEmptyText(maxLength = 28),
      "url" -> nonEmptyText(maxLength = 2000)
        .verifying("must be an URL", url => Validation.url(url))
    )(Read.apply)(Read.unapply)
  )

  def read = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        readData.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            if (Signature.check(post.sig, user.numId.toString, post.url)) {
              FeedDao.get(post.url) match {
                case Some(feed) => Ok(Json.toJson(feed))
                case _ => InternalServerError
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

  def add = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        addData.bindFromRequest.fold(
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
}
