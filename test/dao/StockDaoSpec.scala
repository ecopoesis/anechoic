package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.Play
import scala.xml.XML

class StockDaoSpec extends Specification {
  "StockDao" should {
    "process intraday" in test {
      import play.api.Play.current
      val source = scala.io.Source.fromURL((Play.classloader.getResource("stock-intraday.xml")))
      val xml = XML.loadString(source.mkString)
      source.close()

      val stock = StockDao.processIntraday(xml)
      stock must_!= None
      stock.get.symbol must_== "TSLA"
      stock.get.ticks.length must_== 236

      stock.get.ticks(0).timestamp must_== 1380792659
      stock.get.ticks(0).close must_== 173.2720
      stock.get.ticks(0).high must_== 174.6299
      stock.get.ticks(0).low must_== 173.2700
      stock.get.ticks(0).open must_== 173.7600
      stock.get.ticks(0).volume must_== 1244400
    }
  }

  def test[T](code: =>T) =
    running(FakeApplication())(code)
}