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
  val baseUrl = "http://api.wunderground.com/api/" + Play.current.configuration.getString("api.key.wunderground").get + "/forecast/conditions/astronomy"

  def get(wunder_id: String): Option[Weather] = Cache.getOrElse(CacheKey + wunder_id, CacheTimeout) {
    val svc = url(baseUrl + wunder_id + ".json") <:< Map("User-Agent" -> userAgent)
    val w = Http.configure(_ setFollowRedirects true)(svc OK as.String)
    val json = Json.parse(w())
    Option(parseWeather(json))
  }

  def parseWeather(weather: JsValue): Weather = {
    val current = weather \ "current_observation"
    Weather(
      (current \ "display_location" \ "full").as[String],
      parseFloat(current \ "temp_c").get,
      parseInt(current \ "temp_f").get,
      parseFloat(current \ "pressure_in").get,
      parseInt(current \ "pressure_mb").get,
      (current \ "pressure_trend").as[String],
      (current \ "relative_humidity").as[String],
      (current \ "wind_dir").as[String],
      parseFloat(current \ "wind_mph").get,
      parseFloat(current \ "wind_kph").get,
      parseFloat(current \ "wind_gust_mph").get,
      parseFloat(current \ "wind_gust_kph").get,
      (current \ "icon").as[String],
      parseIsDay(weather \ "moon_phase"),
      parseForecast(weather \ "forecast" \ "simpleforecast")
    )
  }

  def parseFloat(x: JsValue): Option[Float] = {
    x.validate[Float] match {
      case f: JsSuccess[Float] => Option(f.value)
      case _ => {
        x.validate[String] match {
          case s: JsSuccess[String] => Option(s.value.toFloat)
          case _ => None
        }
      }
    }
  }

  def parseInt(x: JsValue): Option[Int] = {
    x.validate[Int] match {
      case i: JsSuccess[Int] => Option(i.value)
      case _ => {
        x.validate[String] match {
          case s: JsSuccess[String] => Option(s.value.toInt)
          case _ => None
        }
      }
    }
  }

  def parseIsDay(astro: JsValue): Boolean = {
    val current_hour = parseInt(astro \ "current_time" \ "hour").get
    val current_min =  parseInt(astro \ "current_time" \ "minute").get
    val sunrise_hour = parseInt(astro \ "sunrise" \ "hour").get
    val sunrise_min = parseInt(astro \ "sunrise" \ "minute").get
    val sunset_hour = parseInt(astro \ "sunset" \ "hour").get
    val sunset_min = parseInt(astro \ "sunset" \ "minute").get

    ((current_hour > sunrise_hour) || (current_hour == sunrise_hour && current_min >= sunrise_min)) &&
    ((current_hour < sunset_hour) || (current_hour == sunset_hour && current_min <= sunset_min))
  }

  def parseForecast(forecast: JsValue): Seq[Forecast] = {
    for (day <- (forecast \ "forecastday").as[List[JsValue]]) yield {
      Forecast(
        (day \ "date" \ "weekday").as[String],
        parseInt(day \ "low" \ "celsius").get,
        parseInt(day \ "low" \ "fahrenheit").get,
        parseInt(day \ "high" \ "celsius").get,
        parseInt(day \ "high" \ "fahrenheit").get,
        (day \ "icon").as[String]
      )
    }
  }
}
