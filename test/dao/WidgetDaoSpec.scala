package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{PasswordInfo, AuthenticationMethod, IdentityId}

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
        WidgetDao.select(1) must_== None
      }

      "create a feed" in testDb {
        WidgetDao.addFeed(testUser, "https://news.ycombinator.com/rss", 10) must_== true
        val widget = WidgetDao.select(1);
        widget must_!= None
        widget.get.id must_== 1L
        widget.get.kind must_== "feed"
        widget.get.properties.size must_== 2
        widget.get.properties.get("url").get must_== "https://news.ycombinator.com/rss"
        widget.get.properties.get("max").get must_== "10"
      }

      "get all widgets for user" in testDb {
        WidgetDao.getAll(1).size must_== 0

        WidgetDao.addFeed(testUser, "https://news.ycombinator.com/rss", 10) must_== true
        WidgetDao.addFeed(testUser, "http://rss.cnn.com/rss/cnn_us.rss", 10) must_== true
        WidgetDao.getAll(-1).size must_== 2
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}