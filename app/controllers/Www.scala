package controllers

import play.api.mvc._
import dao.{CommentDao, FeedDao, StoryDao, UserDao}
import model.{Feed, User}
import helpers.Signature
import helpers.Formatting
import dispatch._
import Defaults._
import Http._
import play.api.{Play, Logger}
import play.api.libs.json.Json

/**
 * web page routes
 */
object Www extends Controller with securesocial.core.SecureSocial {
  val DefaultPageSize = 25
  
  def index = UserAwareAction { implicit request =>
    Ok(views.html.index(request.user, StoryDao.getScoredList(1, DefaultPageSize)))
  }

  def dashboard = UserAwareAction { implicit request =>
    Ok(views.html.dashboard(request.user))
  }

  def feed(url: String, sig: String) = UserAwareAction { implicit request =>
    FeedDao.get(url) match {
      case Some(feed) => Ok(Json.toJson(feed))
      case _ => InternalServerError
    }
  }

  def newest = UserAwareAction { implicit request =>
    Ok(views.html.index(request.user, StoryDao.getNewestList(1, DefaultPageSize)))
  }

  def story(id: Long, seoParam: Option[String]) = UserAwareAction { implicit request =>
    // does this story exist?
    StoryDao.getId(id) match {
      case Some(story) => {
        // do we have the correct SEO URL?
        seoParam match {
          case Some(seo) => {
            if (seo == Formatting.urlify(story.title)) {
              Ok(views.html.story(request.user, story, CommentDao.getComments(id)))
            } else {
              MovedPermanently(Formatting.canonicalUrl(request.host, story))
            }
          }
          case _ => {
            MovedPermanently(Formatting.canonicalUrl(request.host, story))
          }
        }
      }
      case _ => NotFound(views.html.not_found(request.user))
    }
  }

  def comment(id: Long) = UserAwareAction { implicit request =>
    // does this comment exist?
    CommentDao.getComment(id) match {
      case Some(comment) => Ok(views.html.comment(request.user, comment))
      case _ => NotFound(views.html.not_found(request.user))
    }
  }

  def submit = SecuredAction { implicit request =>
    Ok(views.html.submit(Option(request.user)))
  }

  def voteStoryUp(storyId: Long) = SecuredAction { implicit request =>
    request.user match {
      case user: User => {
        request.getQueryString("sig") match {
          case Some(sig) => {
            if (Signature.check(sig, user.identityId.userId, "story", storyId.toString, "up")) {
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
          case _ => BadRequest("missing signature")
        }
      }
      case _ => BadRequest
    }
  }

  def setScheme() = SecuredAction { implicit request =>
    request.user match  {
      case user: User => {
        request.getQueryString("scheme") match {
          case Some(scheme) => {
            request.getQueryString("sig") match {
              case Some(sig) => {
                if (Signature.check(sig, user.identityId.userId, "scheme")) {
                  if (UserDao.setScheme(user.numId, scheme)) {
                    Ok("success")
                  } else {
                    BadRequest("invalid scheme or error inserting into DB")
                  }
                } else {
                  BadRequest("invalid signature")
                }
              }
              case _ => BadRequest("missing signature")
            }
          }
          case _ => BadRequest("missing scheme")
        }
      }
      case _ => BadRequest
    }
  }

  def notFound(url: String) = UserAwareAction { implicit request =>
    NotFound(views.html.not_found(request.user))
  }

  def asset(path: String, file: String, version: String): play.api.mvc.Action[play.api.mvc.AnyContent] = {
    controllers.Assets.at(path, file)
  }
}