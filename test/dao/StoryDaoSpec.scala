package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{PasswordInfo, AuthenticationMethod, IdentityId}

class StoryDaoSpec extends Specification {

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

  "StoryDao" should {
      "have no stories" in testDb {
        StoryDao.getScoredList(1, 10) must have size 0
        StoryDao.getNewestList(1, 10) must have size 0
      }

      "create a story" in testDb {
        StoryDao.add("Fake Story", "http://www.fake.com", 1) must beEqualTo(1L)
        val story = StoryDao.getId(1)
        story must_!= None
        story.get.id must_== 1L
        story.get.title must_== "Fake Story"
        story.get.url must_== "http://www.fake.com"
        story.get.score must_== StoryDao.DefaultScore
        story.get.comments must_== 0
      }

      "vote for a story" in testDb {
        UserDao.insert(testUser)
        StoryDao.add("Fake Story", "http://www.fake.com", 1) must beEqualTo(1L)
        StoryDao.vote(1, 1, 42) must_== true
        val story = StoryDao.getId(1)
        story.get.score must_== StoryDao.DefaultScore + 42
      }

      "vote for a nonexistant story" in testDb {
        StoryDao.getId(1) must_== None
        StoryDao.vote(1, 1, 42) must_== false
      }

      "have not voted" in testDb {
        StoryDao.voted(1, 1) must_== false
      }

      "have voted" in testDb {
        UserDao.insert(testUser)
        StoryDao.add("Fake Story", "http://www.fake.com", 1) must beEqualTo(1L)
        StoryDao.vote(1, 1, 42) must_== true
        StoryDao.voted(1, 1) must_== true
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}