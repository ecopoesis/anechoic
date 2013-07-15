package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.StoryDao
import model.User

/**
 * form submission
 */
object Forms extends Controller with securesocial.core.SecureSocial {

  val storyForm = Form(
    tuple(
      "title" -> text,
      "url" -> text
    )
  )

  /**
   * @todo validation
   * @todo user
   */
  def story = SecuredAction { implicit request =>
    val form = storyForm.bindFromRequest.data
    val title = form("title")
    val url = form("url")
    val foo = request.user match {
      case user : User => StoryDao.add(title, url, user.numId)
      case _ => None
    }

    foo match {
      case a: Int => Ok(a.toString)
      case _ => Ok("something else")
    }
  }
}
