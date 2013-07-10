package controllers

import play.api._
import play.api.mvc._

/**
 * web page routes
 */
object Www extends Controller with securesocial.core.SecureSocial {
  
  def index = UserAwareAction { implicit request =>
    Ok(views.html.index(request.user))
  }

  def submit = SecuredAction { implicit request =>
    Ok(views.html.submit())
  }
  
}