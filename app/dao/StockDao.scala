package dao

import helpers.Http
import scala.xml.{Elem, Text, XML}
import model.{Tick, Stock}
import play.api.cache.Cache
import play.api.Play.current

object StockDao {
  val CacheKey = "stock_"
  val CacheTimeout = 60 * 5

  val intradayUrl = "http://chartapi.finance.yahoo.com/instrument/1.0/$SYMBOL$/chartdata;type=quote;range=1d/xml"

  def get(symbol: String, range: String): Option[Stock] = Cache.getOrElse(CacheKey + range + "_" + symbol, CacheTimeout) {
    range match {
      case "1d" => intraday(symbol)
      case _ => None
    }
  }

  def intraday(symbol: String): Option[Stock] = {
    val xml = XML.loadString(Http.get(intradayUrl.replace("$SYMBOL$", symbol)))
    processIntraday(xml)
  }

  def processIntraday(xml: Elem): Option[Stock] = {
    if ((xml \\ "error").length > 0) {
      None
    } else {
      val offset = (xml \\ "feature" filter( _ \ "@name" contains Text("gmtoffset"))).text.toInt
      val ticks = for (p <- (xml \\ "p")) yield {
        Tick(
          (p \ "@ref").text.toInt + offset,
          (p \\ "v")(0).text.toDouble,
          (p \\ "v")(1).text.toDouble,
          (p \\ "v")(2).text.toDouble,
          (p \\ "v")(3).text.toDouble,
          (p \\ "v")(4).text.toDouble
        )
      }

      Some(Stock(
        (xml \\ "feature" filter( _ \ "@name" contains Text("ticker"))).text.toUpperCase,
        ticks
      ))
    }
  }
}
