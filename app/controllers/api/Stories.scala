package controllers.api

import play.api.mvc._
import play.api.libs.json.Json
import dao.StoryDao

object Stories extends Controller with securesocial.core.SecureSocial {
  
  def list = Action {
    Ok(Json.toJson(StoryDao.get(1, 1)))
  }

  def post(title: String, url: String) = SecuredAction(ajaxCall = true) { implicit request =>
    Ok("hello")
   // Ok(Json.toJson(StoriesDao.add(title, url)))
  }
}