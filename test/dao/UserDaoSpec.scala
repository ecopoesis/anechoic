package dao

import org.specs2.mutable.Specification
import org.specs2.matcher._
import play.api.test._
import play.api.test.Helpers._

class UserDaoSpec extends Specification {

  "UserDao" should {
      "user 'foo' shouldn't exist" in testDb {
        UserDao.getByUsername("foo") must_== None
      }
  }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}