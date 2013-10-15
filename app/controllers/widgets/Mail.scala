package controllers.widgets

import play.api.mvc.{Cookie, Controller}
import play.api.data.Form
import play.api.data.Forms._
import helpers.{Crypto, Signature, Validation}
import model.User
import dao.{WidgetDao, FeedDao}
import play.api.libs.json.Json
import play.api.Logger

object Mail extends Controller with securesocial.core.SecureSocial {
  case class Add(host: String, username: String, password: String, port: Int, ssl: Boolean)
  case class Read(sig: String, url: String)

  val addData = Form(
    mapping(
      "host" -> nonEmptyText(maxLength = 100),
      "username" -> nonEmptyText(maxLength = 100),
      "password" -> nonEmptyText(maxLength = 100),
      "port" -> number(min=1),
      "ssl" -> boolean
    )(Add.apply)(Add.unapply)
  )

  val readData = Form(
    mapping(
      "sig" -> nonEmptyText(maxLength = 28),
      "url" -> nonEmptyText(maxLength = 2000)
        .verifying("must be an URL", url => Validation.url(url))
    )(Read.apply)(Read.unapply)
  )

  def read = UserAwareAction { implicit request =>
    readData.bindFromRequest.fold(
      errors => BadRequest(errors.toString),
      post => {
        if (Signature.check(post.sig, post.url)) {
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

  def add = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        addData.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            request.cookies.get("q") match {
              case Some(q: Cookie) => {
                if (new WidgetDao(Crypto.rsaDecrypt(q.value)).addMail(user, post.host, post.username, post.password, post.port, post.ssl) > 0) {
                  Ok
                } else {
                  InternalServerError
                }
              }
              case _ => BadRequest("no q value")
            }
          }
        )
      }
      case _ => BadRequest("invalid user object")
    }
  }
}
