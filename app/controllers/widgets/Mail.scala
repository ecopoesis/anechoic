package controllers.widgets

import play.api.mvc.{Cookie, Controller}
import play.api.data.Form
import play.api.data.Forms._
import helpers.{Crypto, Signature, Validation}
import model.User
import dao.{MailDao, WidgetDao, FeedDao}
import play.api.libs.json.Json
import play.api.Logger
import javax.mail.MessagingException

object Mail extends Controller with securesocial.core.SecureSocial {
  case class Add(host: String, username: String, password: String, port: Int, ssl: Boolean)
  case class Read(sig: String, id: Int)

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
      "id" -> number
    )(Read.apply)(Read.unapply)
  )

  def read = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        readData.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            if (Signature.check(post.sig, post.id.toString)) {
              request.cookies.get("q") match {
                case Some(q: Cookie) => {
                  new WidgetDao(Crypto.rsaDecrypt(q.value)).select(post.id) match {
                    case Some(widget) => {
                      if (widget.userId == user.numId) {
                        try {
                          val messages = MailDao.getMessages(widget.properties.get("host").get, widget.properties.get("username").get, widget.properties.get("password").get, widget.properties.get("port").get.toInt, widget.properties.get("ssl").get.toBoolean)
                          Ok(Json.toJson(messages))
                        } catch {
                          case me: MessagingException => Ok(Json.toJson(model.Error(me.getMessage)))
                        }
                      } else {
                        Forbidden
                      }
                    }
                    case _ => BadRequest("invalid widget")
                  }
                }
                case _ => BadRequest("no q value")
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
