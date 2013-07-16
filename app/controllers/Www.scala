package controllers

import play.api._
import play.api.mvc._
import dao.StoryDao
import dao.CommentDao

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
}