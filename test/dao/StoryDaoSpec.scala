package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class StoryDaoSpec extends Specification {

  "StoryDao" should {
      "have no stories" in testDb {
        StoryDao.getList(1, 10) must have size 0
      }

      "create a story" in testDb {
        StoryDao.add("Fake Story", "http://www.fake.com", 1) must beEqualTo(1L)
        val story = StoryDao.getId(1)
        story must_!= None
        story.get.id must_== 1L
        story.get.title must_== "Fake Story"
        story.get.url must_== "http://www.fake.com"
      }

      "create stories in order" in testDb {
        StoryDao.getList(1, 10) must have size 0
        StoryDao.add("Fake Story", "http://www.fake.com", 1) must beEqualTo(1L)
        StoryDao.getList(1, 10) must have size 1
        StoryDao.add("Example", "http://www.example.com", 1) must_== 2L
        StoryDao.getList(1, 10) must have size 2
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}