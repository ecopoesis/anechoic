package controllers.api

import play.api.mvc._
import play.api.libs.json.Json
import dao.StoriesDao

/**
 * api controllers
 */
object Stories extends Controller {
  
  def getStories = Action {
    Ok(Json.toJson(StoriesDao.get(1, 1)))
  }
  
}