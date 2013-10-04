package model

import play.api.libs.json._

case class Tick (
    timestamp: Long,
    close: Double,
    high: Double,
    low: Double,
    open: Double,
    volume: Double
)

case class Stock (
    symbol: String,
    ticks: Seq[Tick],
    min: Long,
    max: Long,
    previousClose: Double
)

object Tick {
  implicit val tickReads = Json.reads[Tick]
  implicit val tickWrites = Json.writes[Tick]
}

object Stock {
  implicit val stockReads = Json.reads[Stock]
  implicit val stockWrites = Json.writes[Stock]
}
