package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{PasswordInfo, AuthenticationMethod, IdentityId}
import controllers.Dashboard.WidgetLocation
import org.specs2.execute.Pending

class WidgetDaoSpec extends Specification {
  val testUser = new User(
    -1,
    new IdentityId("jrambo", "junk"),
    "John",
    "Rambo",
    "ignored",
    Option("rambo@army.mil"),
    None,
    AuthenticationMethod.UserPassword,
    None,
    None,
    Option(new PasswordInfo("fakehasher", "fakehashedpassword", Option("fakesalt")))
  )

  "WidgetDao" should {
      "have no widgets" in testDb {
        new WidgetDao(null).select(2) must_== None
      }

      "create a feed" in testDb {
        val dao = new WidgetDao(null)

        dao.addFeed(testUser, "https://news.ycombinator.com/rss", 10) must_== 2L
        val widget = dao.select(2)
        widget must_!= None
        widget.get.id must_== 2L
        widget.get.kind must_== "feed"
        widget.get.properties.size must_== 2
        widget.get.properties.get("url").get must_== "https://news.ycombinator.com/rss"
        widget.get.properties.get("max").get must_== "10"
      }

      "create a mail" in testDb {
        val dao = new WidgetDao("password")

        dao.addMail(testUser, "testhost", "testuser", "testpassword", 1234, true) must_== 2L
        val widget = dao.select(2)
        widget must_!= None
        widget.get.id must_== 2L
        widget.get.kind must_== "mail"
        widget.get.properties.size must_== 5
        widget.get.properties.get("host").get must_== "testhost"
        widget.get.properties.get("username").get must_== "testuser"
        widget.get.properties.get("password").get must_== "testpassword"
        widget.get.properties.get("port").get must_== "1234"
        widget.get.properties.get("ssl").get must_== "true"
      }

      "get all widgets for user" in testDb {
        val dao = new WidgetDao(null)

        dao.getAll(-1).size must_== 0

        dao.addFeed(testUser, "https://news.ycombinator.com/rss", 10) must_== 2L
        dao.addFeed(testUser, "http://rss.cnn.com/rss/cnn_us.rss", 10) must_== 3L
        dao.getAll(-1).size must_== 2
      }

      "save layout" in testDb {
        val dao = new WidgetDao(null)

        // setup
        dao.addFeed(testUser, "https://news.ycombinator.com/rss", 10) must_== 2L
        dao.addFeed(testUser, "http://rss.cnn.com/rss/cnn_us.rss", 10) must_== 3L

        // none of the widgets should have locations
        val w1 = dao.getAll(-1);

        w1(0).properties.get("url").get must_== "https://news.ycombinator.com/rss"
        w1(0).id must_== 2
        w1(0).column must_== None
        w1(0).position must_== None

        w1(1).properties.get("url").get must_== "http://rss.cnn.com/rss/cnn_us.rss"
        w1(1).id must_== 3
        w1(1).column must_== None
        w1(1).position must_== None

        // set locations
        WidgetDao.saveLayout(testUser, List(new WidgetLocation(1, 1, 0), new WidgetLocation(2, 2, 1))) must_== true

        // check locations
        val w2 = dao.getAll(-1)

        w2(0).properties.get("url").get must_== "https://news.ycombinator.com/rss"
        w2(0).id must_== 2
        w2(0).column.get must_== 1
        w2(0).position.get must_== 0

        w2(1).properties.get("url").get must_== "http://rss.cnn.com/rss/cnn_us.rss"
        w2(1).id must_== 3
        w2(1).column.get must_== 2
        w2(1).position.get must_== 1

        // set locations again, leaving off the first this time
        WidgetDao.saveLayout(testUser, List(new WidgetLocation(2, 3, 4))) must_== true

        // check locations
        val w3 = dao.getAll(-1)

        w2(0).properties.get("url").get must_== "https://news.ycombinator.com/rss"
        w2(0).id must_== 2
        w2(0).column must_== None
        w2(0).position must_== None

        w3(1).properties.get("url").get must_== "http://rss.cnn.com/rss/cnn_us.rss"
        w3(1).id must_== 3
        w3(1).column.get must_== 3
        w3(1).position.get must_== 4
      }.pendingUntilFixed("broken because H2 doesn't support transactions. Need to migrate to PGSQL for tests")

      "delete a widget" in testDb {
        val dao = new WidgetDao(null)

        dao.addFeed(testUser, "https://news.ycombinator.com/rss", 10) must_== 2L
        val w1 = dao.select(1)
        w1 must_!= None
        WidgetDao.delete(1)
        val w2 = dao.select(1)
        w2 must_== None
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}