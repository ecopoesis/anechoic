package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class CommentDaoSpec extends Specification {

  "CommentDao" should {
      "have no comments" in testDb {
        CommentDao.getComments(1).storyId must_== 1
        CommentDao.getComments(1).root must have size 0
      }

    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}