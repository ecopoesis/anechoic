package model

case class Weather(
  location: String,
  temp_c: Int,
  temp_f: Int,
  pressure_in: Float,
  pressure_mb: Int,
  pressure_trend: String,
  humidity: Int,
  wind_direction: String,
  wind_mph: Float,
  wind_kph: Float,
  wind_gust_mpg: Float,
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