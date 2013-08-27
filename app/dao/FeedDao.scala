package dao

import play.api.Play.current
import model.Feed
import model.Item
import play.api.Play
import dispatch._
import Defaults._
import scala.xml.{NodeSeq, Elem, XML}
import java.net.URL
import java.util.Locale
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.cache.Cache

object FeedDao {
  val CacheKey = "feed_"
  val CacheTimeout = 60 * 5

  val userAgent = "Anechoic News v" + Play.current.configuration.getString("application.version").get + " - www.anechoicnews.com"
  val iso8601DateFormat = ISODateTimeFormat.dateTimeNoMillis().withLocale(Locale.ENGLISH).withZone(DateTimeZone.UTC);
  val rssDateFormatOffsetTimezone = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z").withLocale(Locale.ENGLISH).withZone(DateTimeZone.UTC);
  val rssDateFormatNoTimezone = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH).withZone(DateTimeZone.UTC);

  def get(uri: String): Option[Feed] = Cache.getOrElse(CacheKey + uri, CacheTimeout) {
    val svc = url(uri) <:< Map("User-Agent" -> userAgent)
    val f = Http.configure(_ setFollowRedirects true)(svc OK as.String)
    val xml = XML.loadString(f())
    if ((xml \\ "channel").length == 0) {
      processAtom(xml)
    } else {
      processRss(xml)
    }
  }

  def processAtom(xml: Elem): Option[Feed] = {
    val feed = xml \\ "feed"
    if (feed.length > 0) {
      val items = for (entry <- (feed.head \\ "entry")) yield {
        Item(
          (entry \\ "title").text,
          (entry \\ "summary").text,
          getLink(entry \\ "link"),
          Option(iso8601DateFormat.parseDateTime((entry \\ "updated").text.trim)),
          (entry \\ "author" \\ "name").text
        )
      }
      Option(Feed(
        (feed \ "title").text,
        (feed \ "subtitle").text,
        getLink(feed \ "link"),
        Option(iso8601DateFormat.parseDateTime((feed \ "updated").text.trim)),
        items
      ))
    } else {
      None
    }
  }

  def processRss(xml: Elem): Option[Feed] = {
    val channel = xml \\ "channel"
    if (channel.length > 0) {
      val items = for (item <- (channel.head \\ "item")) yield {
        Item(
          (item \\ "title").text,
          (item \\ "description").text,
          new URL((item \\ "link").text),
          parseRssDate((item \\ "pubDate").text),
          (item \\ "author").text
        )
      }
      Option(Feed(
        (channel \ "title").text,
        (channel \ "description").text,
        new URL((channel \ "link").text),
        parseRssDate((channel \ "pubDate").text),
        items
      ))
    } else {
      None
    }
  }

  def getLink(node: NodeSeq) = {
    var url = node.filter(n => (n \ "@rel").text == "alternate").map(n => (n \ "@href").text)
    if (url.length == 0) {
      url = node.filter(n => (n \ "@type").text == "text/html").map(n => (n \ "@href").text)
      if (url.length == 0) {
        url = node.filter(n => (n \ "@rel").text != "self").map(n => (n \ "@href").text)
        if (url.length == 0) {
          url = node.map(n => (n \ "@href").text)
        }
      }
    }
    new URL(url.head)
  }

  def parseRssDate(date: String): Option[DateTime] = {
    // grantland has null in some of their pubdates
    if (date.length == 0 || date == "null") {
      return None
    }

    var d = date.trim

    if (d.contains(" ")) {
      // this is probably an RSS format date since it contains spaces
      try {
        Option(rssDateFormatOffsetTimezone.parseDateTime(d))
      } catch {
        case e: IllegalArgumentException => {
          // if the date is longer then 25, assume is has a timezone
          if (d.length > 25) {
            // since timezone names are unparsable, assume this is a UTC time, remove the timezone on the end
            d = d.substring(0, d.lastIndexOf(" "))
          }
          Option(rssDateFormatNoTimezone.parseDateTime(d))
        }
      }
    } else {
      // probably not an rss date, so well try iso8601
      Option(iso8601DateFormat.parseDateTime(d))
    }
  }
}
