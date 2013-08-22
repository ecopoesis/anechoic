package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{PasswordInfo, AuthenticationMethod, IdentityId}

class WidgetDaoSpec extends Specification {
  "WidgetDao" should {
      "have no widgets" in testDb {
        WidgetDao.select(1) must_== None
      }

      "create a widget" in testDb {
        WidgetDao.insert(1, 'feed) must beEqualTo(1L)
        val widget = WidgetDao.select(1);
        widget must_!= None
        widget.get.id must_== 1L
        widget.get.kind must_== 'feed
        widget.get.properties.size must_== 0
      }

      "create a property" in testDb {
        WidgetDao.insert(1, 'feed) must beEqualTo(1L)
        WidgetDao.insertProperty(1, "foo", "bar")
        val widget = WidgetDao.select(1);
        widget must_!= None
        widget.get.id must_== 1L
        widget.get.kind must_== 'feed
        widget.get.properties.size must_== 1
        widget.get.properties.get('foo).get must_== "bar"
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}