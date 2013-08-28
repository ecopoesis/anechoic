package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import scala.xml.XML
import play.api.libs.json.Json

class WeatherDaoSpec extends Specification {
  val json =
    Json.parse(
      """
        |
        |{
        |"response": {
        |"version": "0.1"
        |,"termsofService": "http://www.wunderground.com/weather/api/d/terms.html"
        |,"features": {
        |"forecast": 1
        |,
        |"conditions": 1
        |}
        |}
        |,	"current_observation": {
        |"image": {
        |"url":"http://icons-ak.wxug.com/graphics/wu2/logo_130x80.png",
        |"title":"Weather Underground",
        |"link":"http://www.wunderground.com"
        |},
        |"display_location": {
        |"full":"San Francisco, CA",
        |"city":"San Francisco",
        |"state":"CA",
        |"state_name":"California",
        |"country":"US",
        |"country_iso3166":"US",
        |"zip":"94101",
        |"magic":"1",
        |"wmo":"99999",
        |"latitude":"37.77500916",
        |"longitude":"-122.41825867",
        |"elevation":"47.00000000"
        |},
        |"observation_location": {
        |"full":"SOMA - Near Van Ness, San Francisco, California",
        |"city":"SOMA - Near Van Ness, San Francisco",
        |"state":"California",
        |"country":"US",
        |"country_iso3166":"US",
        |"latitude":"37.773285",
        |"longitude":"-122.417725",
        |"elevation":"49 ft"
        |},
        |"estimated": {
        |},
        |"station_id":"KCASANFR58",
        |"observation_time":"Last Updated on August 28, 12:22 PM PDT",
        |"observation_time_rfc822":"Wed, 28 Aug 2013 12:22:15 -0700",
        |"observation_epoch":"1377717735",
        |"local_time_rfc822":"Wed, 28 Aug 2013 12:22:31 -0700",
        |"local_epoch":"1377717751",
        |"local_tz_short":"PDT",
        |"local_tz_long":"America/Los_Angeles",
        |"local_tz_offset":"-0700",
        |"weather":"Partly Cloudy",
        |"temperature_string":"70.0 F (21.1 C)",
        |"temp_f":70.0,
        |"temp_c":21.1,
        |"relative_humidity":"80%",
        |"wind_string":"Calm",
        |"wind_dir":"ESE",
        |"wind_degrees":105,
        |"wind_mph":0.0,
        |"wind_gust_mph":0,
        |"wind_kph":0.0,
        |"wind_gust_kph":0,
        |"pressure_mb":"1014",
        |"pressure_in":"29.95",
        |"pressure_trend":"+",
        |"dewpoint_string":"64 F (18 C)",
        |"dewpoint_f":64,
        |"dewpoint_c":18,
        |"heat_index_string":"NA",
        |"heat_index_f":"NA",
        |"heat_index_c":"NA",
        |"windchill_string":"NA",
        |"windchill_f":"NA",
        |"windchill_c":"NA",
        |"feelslike_string":"70.0 F (21.1 C)",
        |"feelslike_f":"70.0",
        |"feelslike_c":"21.1",
        |"visibility_mi":"10.0",
        |"visibility_km":"16.1",
        |"solarradiation":"",
        |"UV":"9","precip_1hr_string":"0.00 in ( 0 mm)",
        |"precip_1hr_in":"0.00",
        |"precip_1hr_metric":" 0",
        |"precip_today_string":"0.00 in (0 mm)",
        |"precip_today_in":"0.00",
        |"precip_today_metric":"0",
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"forecast_url":"http://www.wunderground.com/US/CA/San_Francisco.html",
        |"history_url":"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=KCASANFR58",
        |"ob_url":"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=37.773285,-122.417725"
        |}
        |,
        |"forecast":{
        |"txt_forecast": {
        |"date":"8:00 AM PDT",
        |"forecastday": [
        |{
        |"period":0,
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"title":"Wednesday",
        |"fcttext":"Overcast in the morning, then partly cloudy. High of 72F. Winds from the West at 5 to 20 mph.",
        |"fcttext_metric":"Overcast in the morning, then partly cloudy. High of 22C. Windy. Winds from the West at 10 to 30 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":1,
        |"icon":"mostlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif",
        |"title":"Wednesday Night",
        |"fcttext":"Mostly cloudy. Fog overnight. Low of 61F. Winds from the West at 5 to 15 mph.",
        |"fcttext_metric":"Mostly cloudy. Fog overnight. Low of 16C. Breezy. Winds from the West at 10 to 25 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":2,
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"title":"Thursday",
        |"fcttext":"Partly cloudy. Fog early. High of 73F. Winds from the West at 5 to 20 mph.",
        |"fcttext_metric":"Partly cloudy. Fog early. High of 23C. Windy. Winds from the West at 5 to 30 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":3,
        |"icon":"mostlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/mostlycloudy.gif",
        |"title":"Thursday Night",
        |"fcttext":"Mostly cloudy. Fog overnight. Low of 61F. Winds from the WSW at 5 to 20 mph.",
        |"fcttext_metric":"Mostly cloudy. Fog overnight. Low of 16C. Windy. Winds from the WSW at 10 to 30 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":4,
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"title":"Friday",
        |"fcttext":"Clear. Fog early. High of 77F. Winds from the WSW at 5 to 15 mph.",
        |"fcttext_metric":"Clear. Fog early. High of 25C. Breezy. Winds from the WSW at 10 to 20 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":5,
        |"icon":"clear",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/clear.gif",
        |"title":"Friday Night",
        |"fcttext":"Clear. Low of 59F. Winds from the WNW at 5 to 10 mph.",
        |"fcttext_metric":"Clear. Low of 15C. Winds from the WNW at 10 to 15 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":6,
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"title":"Saturday",
        |"fcttext":"Partly cloudy in the morning, then clear. High of 72F. Winds from the West at 5 to 15 mph.",
        |"fcttext_metric":"Partly cloudy in the morning, then clear. High of 22C. Breezy. Winds from the West at 10 to 20 km/h.",
        |"pop":"0"
        |}
        |,
        |{
        |"period":7,
        |"icon":"clear",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/clear.gif",
        |"title":"Saturday Night",
        |"fcttext":"Clear. Low of 59F. Winds from the West at 5 to 10 mph.",
        |"fcttext_metric":"Clear. Low of 15C. Winds from the West at 10 to 15 km/h.",
        |"pop":"0"
        |}
        |]
        |},
        |"simpleforecast": {
        |"forecastday": [
        |{"date":{
        |"epoch":"1377756000",
        |"pretty":"11:00 PM PDT on August 28, 2013",
        |"day":28,
        |"month":8,
        |"year":2013,
        |"yday":239,
        |"hour":23,
        |"min":"00",
        |"sec":0,
        |"isdst":"1",
        |"monthname":"August",
        |"weekday_short":"Wed",
        |"weekday":"Wednesday",
        |"ampm":"PM",
        |"tz_short":"PDT",
        |"tz_long":"America/Los_Angeles"
        |},
        |"period":1,
        |"high": {
        |"fahrenheit":"72",
        |"celsius":"22"
        |},
        |"low": {
        |"fahrenheit":"61",
        |"celsius":"16"
        |},
        |"conditions":"Partly Cloudy",
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"skyicon":"partlycloudy",
        |"pop":0,
        |"qpf_allday": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_day": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_night": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"snow_allday": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_day": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_night": {
        |"in": 0,
        |"cm": 0
        |},
        |"maxwind": {
        |"mph": 17,
        |"kph": 27,
        |"dir": "West",
        |"degrees": 272
        |},
        |"avewind": {
        |"mph": 14,
        |"kph": 22,
        |"dir": "West",
        |"degrees": 268
        |},
        |"avehumidity": 79,
        |"maxhumidity": 88,
        |"minhumidity": 64
        |}
        |,
        |{"date":{
        |"epoch":"1377842400",
        |"pretty":"11:00 PM PDT on August 29, 2013",
        |"day":29,
        |"month":8,
        |"year":2013,
        |"yday":240,
        |"hour":23,
        |"min":"00",
        |"sec":0,
        |"isdst":"1",
        |"monthname":"August",
        |"weekday_short":"Thu",
        |"weekday":"Thursday",
        |"ampm":"PM",
        |"tz_short":"PDT",
        |"tz_long":"America/Los_Angeles"
        |},
        |"period":2,
        |"high": {
        |"fahrenheit":"73",
        |"celsius":"23"
        |},
        |"low": {
        |"fahrenheit":"61",
        |"celsius":"16"
        |},
        |"conditions":"Partly Cloudy",
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"skyicon":"partlycloudy",
        |"pop":0,
        |"qpf_allday": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_day": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_night": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"snow_allday": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_day": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_night": {
        |"in": 0,
        |"cm": 0
        |},
        |"maxwind": {
        |"mph": 17,
        |"kph": 27,
        |"dir": "West",
        |"degrees": 270
        |},
        |"avewind": {
        |"mph": 13,
        |"kph": 21,
        |"dir": "West",
        |"degrees": 268
        |},
        |"avehumidity": 77,
        |"maxhumidity": 87,
        |"minhumidity": 64
        |}
        |,
        |{"date":{
        |"epoch":"1377928800",
        |"pretty":"11:00 PM PDT on August 30, 2013",
        |"day":30,
        |"month":8,
        |"year":2013,
        |"yday":241,
        |"hour":23,
        |"min":"00",
        |"sec":0,
        |"isdst":"1",
        |"monthname":"August",
        |"weekday_short":"Fri",
        |"weekday":"Friday",
        |"ampm":"PM",
        |"tz_short":"PDT",
        |"tz_long":"America/Los_Angeles"
        |},
        |"period":3,
        |"high": {
        |"fahrenheit":"77",
        |"celsius":"25"
        |},
        |"low": {
        |"fahrenheit":"59",
        |"celsius":"15"
        |},
        |"conditions":"Partly Cloudy",
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"skyicon":"mostlysunny",
        |"pop":0,
        |"qpf_allday": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_day": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_night": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"snow_allday": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_day": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_night": {
        |"in": 0,
        |"cm": 0
        |},
        |"maxwind": {
        |"mph": 11,
        |"kph": 18,
        |"dir": "WSW",
        |"degrees": 243
        |},
        |"avewind": {
        |"mph": 8,
        |"kph": 13,
        |"dir": "WSW",
        |"degrees": 239
        |},
        |"avehumidity": 77,
        |"maxhumidity": 88,
        |"minhumidity": 62
        |}
        |,
        |{"date":{
        |"epoch":"1378015200",
        |"pretty":"11:00 PM PDT on August 31, 2013",
        |"day":31,
        |"month":8,
        |"year":2013,
        |"yday":242,
        |"hour":23,
        |"min":"00",
        |"sec":0,
        |"isdst":"1",
        |"monthname":"August",
        |"weekday_short":"Sat",
        |"weekday":"Saturday",
        |"ampm":"PM",
        |"tz_short":"PDT",
        |"tz_long":"America/Los_Angeles"
        |},
        |"period":4,
        |"high": {
        |"fahrenheit":"72",
        |"celsius":"22"
        |},
        |"low": {
        |"fahrenheit":"59",
        |"celsius":"15"
        |},
        |"conditions":"Partly Cloudy",
        |"icon":"partlycloudy",
        |"icon_url":"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif",
        |"skyicon":"mostlysunny",
        |"pop":0,
        |"qpf_allday": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_day": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"qpf_night": {
        |"in": 0.00,
        |"mm": 0.0
        |},
        |"snow_allday": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_day": {
        |"in": 0,
        |"cm": 0
        |},
        |"snow_night": {
        |"in": 0,
        |"cm": 0
        |},
        |"maxwind": {
        |"mph": 11,
        |"kph": 18,
        |"dir": "West",
        |"degrees": 266
        |},
        |"avewind": {
        |"mph": 9,
        |"kph": 14,
        |"dir": "West",
        |"degrees": 268
        |},
        |"avehumidity": 80,
        |"maxhumidity": 91,
        |"minhumidity": 59
        |}
        |]
        |}
        |}
        |}
      """.stripMargin)

  "WeatherDao" should {
    "parse weather" in test {
      val weather = WeatherDao.parseWeather(json)
      weather must_!= None

      weather.location must_== "San Francisco, CA"
      weather.temp_c must_== 21.1f
      weather.temp_f must_== 70
      weather.pressure_in must_== 29.95f
      weather.pressure_mb must_== 1014
      weather.pressure_trend must_== "+"
      weather.humidity must_== "80%"
      weather.wind_direction must_== "ESE"
      weather.wind_mph must_== 0f
      weather.wind_kph must_== 0f
      weather.wind_gust_mph must_== 0f
      weather.wind_gust_kph must_== 0f
      weather.image must_== "partlycloudy"

      weather.forecast.size must_== 4
      weather.forecast(0).day must_== "Wednesday"
      weather.forecast(0).low_c must_== 16
      weather.forecast(0).low_f must_== 61
      weather.forecast(0).high_c must_== 22
      weather.forecast(0).high_f must_== 72
      weather.forecast(0).image must_== "partlycloudy"
    }
  }

  def test[T](code: =>T) =
    running(FakeApplication())(code)
}