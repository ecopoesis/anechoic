package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import scala.xml.XML
import play.api.Play

class FeedDaoRssSpec extends Specification {
  val rss =
    XML.loadString("""
      |<rss version="2.0">
      |<channel>
      | <title>RSS Title</title>
      | <description>This is an example of an RSS feed</description>
      | <link>http://www.someexamplerssdomain.com/main.html</link>
      | <lastBuildDate>Mon, 06 Sep 2010 00:01:00 +0000 </lastBuildDate>
      | <pubDate>Sun, 06 Sep 2009 16:20:00 +0000 </pubDate>
      | <ttl>1800</ttl>
      |
      | <item>
      |  <title>Example entry</title>
      |  <description>Here is some text containing an interesting description.</description>
      |  <link>http://www.wikipedia.org/</link>
      |  <guid>unique string per item</guid>
      |  <pubDate>Sun, 06 Sep 2009 16:20:00 +0000 </pubDate>
      | </item>
      |
      | <item>
      |  <title>Second entry</title>
      |  <description>more description</description>
      |  <link>http://www.example.com/</link>
      |  <guid>unique string per item 2</guid>
      |  <pubDate>Tue, 07 Sep 2010 18:34:00 +0000</pubDate>
      |  <author>John Rambo</author>
      | </item>
      |</channel>
      |</rss>
      |""".stripMargin)

  "FeedDao" should {
    "process rss" in test {
      val feed = FeedDao.processRss(rss)
      feed must_!= None
    }

    "process rss feed with two items" in test {
      val feed = FeedDao.processRss(rss)
      feed.get.items.length must_== 2
    }

    "process rss feed globals correctly" in test {
      val feed = FeedDao.processRss(rss)
      feed.get.title must_== "RSS Title"
      feed.get.description must_== "This is an example of an RSS feed"
      feed.get.link.toString must_== "http://www.someexamplerssdomain.com/main.html"
      feed.get.date.get.toString() must_== "2009-09-06T16:20:00.000Z"
    }

    "process the first entry in the rss feed correctly" in test {
      val feed = FeedDao.processRss(rss)
      feed.get.items(0).title must_== "Example entry"
      feed.get.items(0).description must_== "Here is some text containing an interesting description."
      feed.get.items(0).link.toString must_== "http://www.wikipedia.org/"
      feed.get.items(0).author must_== ""
      feed.get.items(0).date.get.toString() must_== "2009-09-06T16:20:00.000Z"
    }

    "process the second entry in the rss feed correctly" in test {
      val feed = FeedDao.processRss(rss)
      feed.get.items(1).title must_== "Second entry"
      feed.get.items(1).description must_== "more description"
      feed.get.items(1).link.toString must_== "http://www.example.com/"
      feed.get.items(1).author must_== "John Rambo"
      feed.get.items(1).date.get.toString() must_== "2010-09-07T18:34:00.000Z"
    }

    "process arstechnica.com from 2013-08-20" in test {
      import play.api.Play.current
      val feed = FeedDao.processRss(XML.load(Play.classloader.getResource("arstechnica-2013-08-20.rss")))
      feed.get.title must_== "Ars Technica"
      feed.get.date.get.toString() must_== "2013-08-20T18:35:32.000Z"
      feed.get.items.length must_== 25
    }

    "process grantland.com from 2013-08-21" in test {
      import play.api.Play.current
      val feed = FeedDao.processRss(XML.load(Play.classloader.getResource("grantland-2013-08-21.rss")))
      feed.get.title must_== "Grantland: Home Page"
      feed.get.date must_== None
      feed.get.items.length must_== 6

      // check dates
      feed.get.items(0).date.get.toString() must_== "2013-08-20T20:04:25.000Z"
      feed.get.items(3).date must_== None
    }

    "process penny arcade report from 2013-08-21" in test {
      import play.api.Play.current
      val feed = FeedDao.processRss(XML.load(Play.classloader.getResource("pareport-2013-08-21.rss")))
      feed.get.title must_== "Penny Arcade Report"
      feed.get.date must_== None
      feed.get.items.length must_== 10
      feed.get.items(0).date.get.toString() must_== "2013-08-21T17:56:02.000Z"
    }
  }

  def test[T](code: =>T) =
    running(FakeApplication())(code)
}