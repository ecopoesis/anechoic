package dao

import play.api.Play.current
import model.{Weather}
import play.api.Play
import dispatch._
import Defaults._
import play.api.cache.Cache
import play.libs.Json

object WeatherDao {
  val CacheKey = "weather_"
  val CacheTimeout = 60 * 60

  val userAgent = "Anechoic News v" + Play.current.configuration.getString("application.version").get + " - www.anechoicnews.com"
  val baseUrl = "http://api.wunderground.com/api/" + Play.current.configuration.getString("application.api.key.wunderground").get + "/forecast/conditions"

  def get(wunder_id: String): Option[Weather] = Cache.getOrElse(CacheKey + wunder_id, CacheTimeout) {
    val svc = url(baseUrl + wunder_id) <:< Map("User-Agent" -> userAgent)
    val w = Http.configure(_ setFollowRedirects true)(svc OK as.String)
    val json = Json.parse(w())
    None
  }
}
