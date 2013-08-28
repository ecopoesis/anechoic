package model

import play.api.libs.json.Json

case class Weather(
  location: String,
  temp_c: Float,
  temp_f: Int,
  pressure_in: Float,
  pressure_mb: Int,
  pressure_trend: String,
  humidity: String,
  wind_direction: String,
  wind_mph: Float,
  wind_kph: Float,
  wind_gust_mph: Float,
  wind_gust_kph: Float,
  image: String,
  forecast: Seq[Forecast]
)

case class Forecast(
  day: String,
  low_c: Int,
  low_f: Int,
  high_c: Int,
  high_f: Int,
  image: String
)

object Forecast {
  implicit val forecastReads = Json.reads[Forecast]
  implicit val forecastWrites = Json.writes[Forecast]
}

object Weather {
  implicit val weatherReads = Json.reads[Weather]
  implicit val weatherWrites = Json.writes[Weather]
}
