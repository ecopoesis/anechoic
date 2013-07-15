package controllers

import play.api._
import play.api.mvc._
import dao.StoryDao

/**
 * web page routes
 */
object Www extends Controller with securesocial.core.SecureSocial {
  val DefaultPageSize = 25
  
  def index = UserAwareAction { implicit request =>
    Ok(views.html.index(request.user, StoryDao.getList(1, DefaultPageSize)))
  }

  def submit = SecuredAction { implicit request =>
    Ok(views.html.submit(Option(request.user)))
  }
}