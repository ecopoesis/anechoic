package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.StoryDao
import model.User
import org.apache.commons.validator.routines.UrlValidator

case class AddStory(title: String, url: String)

/**
 * form submission
 */
object Forms extends Controller with securesocial.core.SecureSocial {

  val storyPost = Form(
    mapping(
      "title" -> nonEmptyText(maxLength = 100),
      "url" -> nonEmptyText(maxLength = 2000)
        .verifying("must be an URL", url => Validate.url(url))
    )(AddStory.apply)(AddStory.unapply)
  )

  /**
   * @todo bubble errors up to
   */
  def story = SecuredAction { implicit request =>
    storyPost.bindFromRequest.fold(
      errors => Ok("form errors"),
      post => {
        val foo = request.user match {
          case user : User => StoryDao.add(post.title, post.url, user.numId)
          case _ => None
        }

        foo match {
          case a: Int => Ok(a.toString)
          case _ => Ok("something else")
        }
      }
    )
  }
}

object Validate {
  def url(url: String): Boolean = {
    val validator = new UrlValidator(Array("http", "https"))
    validator.isValid(url)
  }
}