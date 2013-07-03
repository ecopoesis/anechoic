package controllers

import play.api.mvc._

/**
 * api controllers
 */
object Api extends Controller {
  
  def getStories = Action {
    Ok("stories")
  }
  
}