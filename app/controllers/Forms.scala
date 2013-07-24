package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Controller
import dao.StoryDao
import dao.CommentDao
import model.User
import org.apache.commons.validator.routines.UrlValidator
import play.api.Logger

case class AddStory(title: String, url: String)
case class AddComment(storyId: Long, parentId: Option[Long], text: String)

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

  val commentPost = Form(
    mapping(
      "storyId" -> longNumber,
      "parentId" -> optional(longNumber),
      "comment" -> nonEmptyText
    )(AddComment.apply)(AddComment.unapply)
  )

  /**
   * @todo fix form errors
   */
  def story = SecuredAction { implicit request =>
    storyPost.bindFromRequest.fold(
      errors => Ok(errors.toString),
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

  /**
   * @todo add some validation of story/parent (hash?)
   * @todo clean up incoming comments
   * @todo markdown?
   * @todo fix form errors
   */
  def comment = SecuredAction { implicit request =>
    commentPost.bindFromRequest.fold(
      errors => Ok(errors.toString),
      post => {
        val foo = request.user match {
          case user: User => CommentDao.add(post.storyId, post.parentId, post.text, user.numId)
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