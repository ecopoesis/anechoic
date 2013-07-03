package controllers

import play.api._
import play.api.mvc._

/**
 * web page routes
 */
object Www extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
  
}