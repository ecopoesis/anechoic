package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{PasswordInfo, AuthenticationMethod, UserId}

class CommentDaoSpec extends Specification {
  val testUser = new User(
    -1,
    new UserId("jrambo", "junk"),
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

  "CommentDao" should {
      "have no comments" in testDb {
        UserDao.insert(testUser) must beEqualTo(1L)
        CommentDao.getComments(1).storyId must_== 1
        CommentDao.getComments(1).root must have size 0
      }

      "add a toplevel nest" in testDb {
        UserDao.insert(testUser) must beEqualTo(1L)
        CommentDao.add(1, None, "this is a nest", 1)
        val comment = CommentDao.getComment(1)
        comment must_!= None
        comment.get.parentId must_== None
        comment.get.storyId must_== 1
        comment.get.text must_== "this is a nest"
      }

    "add a child nest" in testDb {
      UserDao.insert(testUser) must beEqualTo(1L)
      CommentDao.add(4, Option(42), "this is another nest", 1)
      val comment = CommentDao.getComment(1)
      comment must_!= None
      comment.get.parentId must_!= None
      comment.get.parentId.get must_== 42
      comment.get.storyId must_== 4
      comment.get.text must_== "this is another nest"
    }
  }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}