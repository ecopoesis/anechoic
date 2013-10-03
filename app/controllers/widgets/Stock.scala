package controllers.widgets

import play.api.mvc.Controller
import play.api.data.Form
import play.api.data.Forms._
import model.User
import dao.{StockDao, WeatherDao, WidgetDao}
import helpers.{Signature}
import play.api.libs.json.Json

object Stock extends Controller with securesocial.core.SecureSocial {
  case class Add(symbol: String, range: String)
  case class Read(symbol: String, range: String, sig: String)

  val addData = Form(
    mapping(
      "symbol" -> nonEmptyText(maxLength = 10),
      "range" -> nonEmptyText(maxLength = 10)
    )(Add.apply)(Add.unapply)
  )

  val readData = Form(
    mapping(
      "symbol" -> nonEmptyText(maxLength = 10),
      "range" -> nonEmptyText(maxLength = 10),
      "sig" -> nonEmptyText(maxLength = 28)
    )(Read.apply)(Read.unapply)
  )

  def read = UserAwareAction { implicit request =>
    readData.bindFromRequest.fold(
      errors => BadRequest(errors.toString),
      post => {
        if (Signature.check(post.sig, post.symbol)) {
          StockDao.get(post.symbol, post.range) match {
            case Some(stock) => Ok(Json.toJson(stock))
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
            if (WidgetDao.addStock(user, post.symbol, post.range) > 0) {
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
