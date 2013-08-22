package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.{WidgetDao, StoryDao, CommentDao}
import model.User
import helpers.Validation
import play.api.libs.json.Json

/**
 * dashboard controllers
 */
object Dashboard extends Controller with securesocial.core.SecureSocial {
  case class AddFeedPost(max: Int, url: String)

  val addFeedPost = Form(
    mapping(
      "max" -> number,
      "url" -> nonEmptyText(maxLength = 2000)
        .verifying("must be an URL", url => Validation.url(url))
    )(AddFeedPost.apply)(AddFeedPost.unapply)
  )

  def getLayout = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        Ok(Json.toJson(WidgetDao.getAll(user.numId)))
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
}
