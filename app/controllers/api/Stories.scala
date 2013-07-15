package controllers.api

import play.api.mvc._
import play.api.libs.json.Json
import dao.StoryDao

object Stories extends Controller with securesocial.core.SecureSocial {
  
  def list = Action {
    Ok("foo")
    //Ok(Json.toJson(StoryDao.getList(1, 1)))
  }

  def post(title: String, url: String) = SecuredAction(ajaxCall = true) { implicit request =>
    Ok("hello")
   // Ok(Json.toJson(StoriesDao.add(title, url)))
  }
}