package controllers

import play.api.mvc._
import dao.StoryDao
import dao.CommentDao
import model.{Story, User}
import helpers.Signature
import helpers.Formatting
import play.api.Logger

/**
 * web page routes
 */
object Www extends Controller with securesocial.core.SecureSocial {
  val DefaultPageSize = 25
  
  def index = UserAwareAction { implicit request =>
    Ok(views.html.index(request.user, StoryDao.getList(1, DefaultPageSize)))
  }

  def story(id: Long, seoParam: Option[String]) = UserAwareAction { implicit request =>
    // does this story exist?
    StoryDao.getId(id).get match {
      case story: Story => {
        // do we have the correct SEO URL?
        seoParam match {
          case Some(seo) => {
            if (seo == Formatting.urlify(story.title)) {
              Ok(views.html.story(request.user, story, CommentDao.getComments(id)))
            } else {
              MovedPermanently(Formatting.canonicalUrl(request.host, story))
            }
          }
          case None => {
            MovedPermanently(Formatting.canonicalUrl(request.host, story))
          }
        }
      }
      case _ => NotFound(views.html.not_found(request.user))
    }
  }

  // @todo handle 404
  def comment(storyId: Long, parentId: Long) = UserAwareAction { implicit request =>
    Ok(views.html.comment(request.user, StoryDao.getId(storyId).get, CommentDao.getComment(parentId).get))
  }

  def submit = SecuredAction { implicit request =>
    Ok(views.html.submit(Option(request.user)))
  }

  def voteStoryUp(storyId: Long) = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        request.getQueryString("sig") match {
          case sig: Some[String] => {
            if (sig.get == Signature.sign("story", storyId.toString, "up")) {
              if (!StoryDao.voted(storyId, user.numId)) {
                if (StoryDao.vote(storyId, user.numId, 1)) {
                  Ok("success")
                } else {
                  BadRequest("error inserting into DB")
                }
              } else {
                BadRequest("already voted")
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

  def notFound(url: String) = UserAwareAction { implicit request =>
    NotFound(views.html.not_found(request.user))
  }
}