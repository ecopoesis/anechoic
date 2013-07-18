package controllers

import play.api._
import play.api.mvc._
import dao.StoryDao
import dao.CommentDao
import model.User
import securesocial.core.SignUpEvent
import service.Signature

/**
 * web page routes
 */
object Www extends Controller with securesocial.core.SecureSocial {
  val DefaultPageSize = 25
  
  def index = UserAwareAction { implicit request =>
    Ok(views.html.index(request.user, StoryDao.getList(1, DefaultPageSize)))
  }

  // @todo handle 404
  def story(id: Long) = UserAwareAction { implicit request =>
    Ok(views.html.story(request.user, StoryDao.getId(id).get, CommentDao.getComments(id)))
  }

  // @todo handle 404
  def comment(storyId: Long, parentId: Long) = UserAwareAction { implicit request =>
    Ok(views.html.comment(request.user, StoryDao.getId(storyId).get, CommentDao.getComment(parentId).get))
  }

  def submit = SecuredAction { implicit request =>
    Ok(views.html.submit(Option(request.user)))
  }

  // @todo check if vote already exists
  def voteStoryUp(storyId: Long) = SecuredAction { implicit request =>
    Logger.debug("sig: " + request.getQueryString("sig"))
    request.user match {
      case user: User => {
        request.getQueryString("sig") match {
          case sig: Some[String] => {
            if (sig.get == Signature.sign("story", storyId.toString, "up")) {
              if (StoryDao.vote(storyId, user.numId, 1)) {
                Ok("success")
              } else {
                BadRequest("error inserting into DB")
              }
            } else {
              BadRequest("invalid signature")
            }
          }
          case _ => BadRequest("missing request")
        }
      }
      case _ => BadRequest
    }
  }
}