package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{PasswordInfo, AuthenticationMethod, IdentityId}
import scala.xml.XML
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone

class FeedDaoAtomSpec extends Specification {
  val atom =
    XML.loadString("""
      |<feed xmlns="http://www.w3.org/2005/Atom">
      |        <title>Example Feed</title>
      |        <subtitle>A subtitle.</subtitle>
      |        <link href="http://example.org/feed/" rel="self" />
      |        <link href="http://example.org/" />
      |        <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>
      |        <updated>2003-12-13T18:30:02Z</updated>
      |        <entry>
      |                <title>Atom-Powered Robots Run Amok</title>
      |                <link href="http://example.org/2003/12/13/atom03" />
      |                <link rel="alternate" type="text/html" href="http://example.org/2003/12/13/atom03.html"/>
      |                <link rel="edit" href="http://example.org/2003/12/13/atom03/edit"/>
      |                <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
      |                <updated>2003-12-13T18:30:02Z</updated>
      |                <summary>Some text.</summary>
      |                <author>
      |                      <name>John Doe</name>
      |                      <email>johndoe@example.com</email>
      |                </author>
      |        </entry>
      |        <entry>
      |                <title>I Become Death</title>
      |                <link type="text/html" href="http://example.org/boom" />
      |                <id>urn:uuid:1225c695-cfb8-4ebb-zzzz-80da344efa6a</id>
      |                <updated>2006-11-12T18:30:02Z</updated>
      |                <summary>Summary!</summary>
      |                <author>
      |                      <name>Robert Oppenheimer</name>
      |                </author>
      |        </entry>
      |</feed>
    """.stripMargin)

  "FeedDao" should {
    "process atom" in test {
      val feed = FeedDao.processAtom(atom)
      feed must_!= None
    }

    "process atom feed with two items" in test {
      val feed = FeedDao.processAtom(atom)
      feed.get.items.length must_== 2
    }

    "process atom feed globals correctly" in test {
      val feed = FeedDao.processAtom(atom)
      feed.get.title must_== "Example Feed"
      feed.get.description must_== "A subtitle."
      feed.get.link.toString must_== "http://example.org/"
      feed.get.date.toString() must_== "2003-12-13T18:30:02.000Z"
    }

    "process the first entry in the atom feed correctly" in test {
      val feed = FeedDao.processAtom(atom)
      feed.get.items(0).title must_== "Atom-Powered Robots Run Amok"
      feed.get.items(0).description must_== "Some text."
      feed.get.items(0).link.toString must_== "http://example.org/2003/12/13/atom03.html"
      feed.get.items(0).author must_== "John Doe"
      feed.get.items(0).date.toString() must_== "2003-12-13T18:30:02.000Z"
    }

    "process the second entry in the atom feed correctly" in test {
      val feed = FeedDao.processAtom(atom)
      feed.get.items(1).title must_== "I Become Death"
      feed.get.items(1).description must_== "Summary!"
      feed.get.items(1).link.toString must_== "http://example.org/boom"
      feed.get.items(1).author must_== "Robert Oppenheimer"
      feed.get.items(1).date.toString() must_== "2006-11-12T18:30:02.000Z"
    }
  }

  def test[T](code: =>T) =
    running(FakeApplication())(code)
}