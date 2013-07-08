package controllers.api

import play.api.mvc._
import play.api.libs.json.Json
import dao.StoriesDao

object Stories extends Controller {
  
  def list = Action {
    Ok(Json.toJson(StoriesDao.get(1, 1)))
  }

  def post(title: String, url: String) = Action {
    Ok("hello")
   // Ok(Json.toJson(StoriesDao.add(title, url)))
  }
}