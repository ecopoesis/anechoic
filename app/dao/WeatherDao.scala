package dao

import play.api.Play.current
import model.{Forecast, Weather}
import play.api.Play
import dispatch._
import Defaults._
import play.api.cache.Cache
import play.api.libs.json._

object WeatherDao {
  val CacheKey = "weather_"
  val CacheTimeout = 60 * 60

  val userAgent = "Anechoic News v" + Play.current.configuration.getString("application.version").get + " - www.anechoicnews.com"
  val baseUrl = "http://api.wunderground.com/api/"// + Play.current.configuration.getString("application.api.key.wunderground").get + "/forecast/conditions"

  def get(wunder_id: String): Option[Weather] = Cache.getOrElse(CacheKey + wunder_id, CacheTimeout) {
    val svc = url(baseUrl + wunder_id) <:< Map("User-Agent" -> userAgent)
    val w = Http.configure(_ setFollowRedirects true)(svc OK as.String)
    val json = Json.parse(w())
    Option(parseWeather(json))
  }

  def parseWeather(weather: JsValue): Weather = {
    val current = weather \ "current_observation"
    Weather(
      (current \ "display_location" \ "full").as[String],
      (current \ "temp_c").as[Float],
      (current \ "temp_f").as[Int],
      (current \ "pressure_in").as[String].toFloat,
      (current \ "pressure_mb").as[String].toInt,
      (current \ "pressure_trend").as[String],
      (current \ "relative_humidity").as[String],
      (current \ "wind_dir").as[String],
      (current \ "wind_mph").as[Float],
      (current \ "wind_kph").as[Float],
      (current \ "wind_gust_mph").as[Float],
      (current \ "wind_gust_kph").as[Float],
      (current \ "icon").as[String],
      parseForecast(weather \ "forecast" \ "simpleforecast")
    )
  }

  def parseForecast(forecast: JsValue): Seq[Forecast] = {
    for (day <- (forecast \ "forecastday").as[List[JsValue]]) yield {
      Forecast(
        (day \ "date" \ "weekday").as[String],
        (day \ "low" \ "celsius").as[String].toInt,
        (day \ "low" \ "fahrenheit").as[String].toInt,
        (day \ "high" \ "celsius").as[String].toInt,
        (day \ "high" \ "fahrenheit").as[String].toInt,
        (day \ "icon").as[String]
      )
    }
  }
}
