package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class StoriesDaoSpec extends Specification {

  "StoriesDao" should {
      "have no stories" in testDb {
        StoriesDao.get(0, 0) must have size 0
      }

      "create a story" in testDb {
        StoriesDao.add("Fake Story", "www.fake.com") must beEqualTo(1L)
      }

      "create stories in order" in testDb {
        StoriesDao.get(0, 0) must have size 0
        StoriesDao.add("Fake Story", "www.fake.com") must beEqualTo(1L)
        StoriesDao.get(0, 0) must have size 1
        StoriesDao.add("Example", "www.example.com") must_== 2L
        StoriesDao.get(0, 0) must have size 2
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=MySQL"
    )))(code)
}