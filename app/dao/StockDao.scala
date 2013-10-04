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
    val xml = XML.loadString(Http.get(intradayUrl.replace("$SYMBOL$", java.net.URLEncoder.encode(symbol, "utf8"))))
    processIntraday(xml)
  }

  def processIntraday(xml: Elem): Option[Stock] = {
    if ((xml \\ "error").length > 0) {
      None
    } else {
      val min = (xml \\ "data-series" \ "reference-meta" \ "min").text.toLong
      val max = (xml \\ "data-series" \ "reference-meta" \ "max").text.toLong
      val ticks = for (p <- (xml \\ "p")) yield {
        Tick(
          (p \ "@ref").text.toLong,
          (p \\ "v")(0).text.toDouble,
          (p \\ "v")(1).text.toDouble,
          (p \\ "v")(2).text.toDouble,
          (p \\ "v")(3).text.toDouble,
          (p \\ "v")(4).text.toDouble
        )
      }

      Some(Stock(
        (xml \\ "feature" filter( _ \ "@name" contains Text("ticker"))).text.toUpperCase,
        ticks,
        min,
        max,
        (xml \\ "feature" filter( _ \ "@name" contains Text("previous_close"))).text.toDouble
      ))
    }
  }
}
