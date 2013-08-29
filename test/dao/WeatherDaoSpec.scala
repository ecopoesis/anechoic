package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import scala.xml.XML
import play.api.libs.json.Json
import play.api.Play

class WeatherDaoSpec extends Specification {
  "WeatherDao" should {
    "parse weather" in test {
      import play.api.Play.current
      val source = scala.io.Source.fromURL((Play.classloader.getResource("weather.json")))
      val json = Json.parse(source.mkString)
      source.close()

      val weather = WeatherDao.parseWeather(json)
      weather must_!= None

      weather.location must_== "Sydney, New South Wales"
      weather.temp_c must_== 18.0f
      weather.temp_f must_== 64
      weather.pressure_in must_== 29.95f
      weather.pressure_mb must_== 1014
      weather.pressure_trend must_== "0"
      weather.humidity must_== "64%"
      weather.wind_direction must_== "North"
      weather.wind_mph must_== 10.0f
      weather.wind_kph must_== 17.0f
      weather.wind_gust_mph must_== 0f
      weather.wind_gust_kph must_== 0f
      weather.image must_== "clear"
      weather.isDay must_== false

      weather.forecast.size must_== 4
      weather.forecast(0).day must_== "Friday"
      weather.forecast(0).low_c must_== 13
      weather.forecast(0).low_f must_== 55
      weather.forecast(0).high_c must_== 25
      weather.forecast(0).high_f must_== 77
      weather.forecast(0).image must_== "rain"
    }
  }

  def test[T](code: =>T) =
    running(FakeApplication())(code)
}