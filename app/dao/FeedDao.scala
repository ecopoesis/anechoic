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

object FeedDao {

  val userAgent = "Anechoic News v" + Play.current.configuration.getString("application.version") + " - www.anechoicnews.com"
  val iso8601DateFormat = ISODateTimeFormat.dateTimeNoMillis().withLocale(Locale.ENGLISH).withZone(DateTimeZone.UTC);
  val rssDateFormatOffsetTimezone = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z").withLocale(Locale.ENGLISH).withZone(DateTimeZone.UTC);
  val rssDateFormatNoTimezone = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH).withZone(DateTimeZone.UTC);

  def get(uri: String): Option[Feed] = {
    val svc = url(uri) <:< Map("User-Agent" -> userAgent)
    val f = Http(svc OK as.String)
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
          iso8601DateFormat.parseDateTime((entry \\ "updated").text.trim),
          (entry \\ "author" \\ "name").text
        )
      }
      Option(Feed(
        (feed \ "title").text,
        (feed \ "subtitle").text,
        getLink(feed \ "link"),
        iso8601DateFormat.parseDateTime((feed \ "updated").text.trim),
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

  def parseRssDate(date: String): DateTime = {
    var d = date.trim
    try {
      rssDateFormatOffsetTimezone.parseDateTime(d)
    } catch {
      case e: IllegalArgumentException => {
        // since timezone names are unparsable, assume this is a UTC time, remove the timezone on the end
        d = d.substring(0, d.lastIndexOf(" "))
        rssDateFormatNoTimezone.parseDateTime(d)
      }
    }
  }
}
