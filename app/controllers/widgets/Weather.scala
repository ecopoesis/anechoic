package controllers.widgets

import play.api.mvc.{Cookie, Controller}
import play.api.data.Form
import play.api.data.Forms._
import model.User
import dao.{WeatherDao, WidgetDao}
import helpers.{Crypto, Signature}
import play.api.libs.json.Json
import java.net.URLEncoder

object Weather extends Controller with securesocial.core.SecureSocial {
  case class Add(city: String, wunderId: String)
  case class Read(sig: String, wunderId: String)

  val addData = Form(
    mapping(
      "city" -> nonEmptyText(maxLength = 100),
      "wunder_id" -> nonEmptyText(maxLength = 100)
    )(Add.apply)(Add.unapply)
  )

  val readData = Form(
    mapping(
      "sig" -> nonEmptyText(maxLength = 28),
      "url" -> nonEmptyText(maxLength = 100)
    )(Read.apply)(Read.unapply)
  )

  def read = UserAwareAction { implicit request =>
    readData.bindFromRequest.fold(
      errors => BadRequest(errors.toString),
      post => {
        if (Signature.check(post.sig, post.wunderId)) {
          WeatherDao.get(post.wunderId) match {
            case Some(weather) => Ok(Json.toJson(weather))
            case _ => InternalServerError
          }
        } else {
          BadRequest("invalid signature")
        }
      }
    )
  }

  def add = SecuredAction { implicit  request =>
    request.user match {
      case user: User => {
        addData.bindFromRequest.fold(
          errors => BadRequest(errors.toString),
          post => {
            request.cookies.get("q") match {
              case Some(q: Cookie) => {
                if (new WidgetDao(Crypto.rsaDecrypt(q.value)).addWeather(user, post.city, post.wunderId) > 0) {
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

  def city(cb: String, query: String) = SecuredAction { implicit  request =>
    Ok(helpers.Http.get("http://autocomplete.wunderground.com/aq?cb=" + URLEncoder.encode(cb, "UTF-8") + "&query=" + URLEncoder.encode(query, "UTF-8")))
  }
}
