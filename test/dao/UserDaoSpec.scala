package dao

import org.specs2.mutable.Specification
import org.specs2.matcher._
import play.api.test._
import play.api.test.Helpers._
import model.User
import securesocial.core.{AuthenticationMethod, PasswordInfo, UserId}
import securesocial.core.providers.UsernamePasswordProvider

class UserDaoSpec extends Specification {
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

  "UserDao" should {
    "user 'foo' shouldn't exist" in testDb {
      UserDao.getByUsername("foo") must_== None
    }

    "email 'fake@example.com' shouldn't exist" in testDb {
      UserDao.getByEmail("fake@example.com") must_== None
    }

    "create a user" in testDb {
      UserDao.getByUsername("jrambo") must_== None
      UserDao.insert(testUser) must beEqualTo(1L)
      val user = UserDao.getByUsername(testUser.id.id)
      user must_!= None
      user.get.numId must_== 1
      user.get.id.id must_== "jrambo"
      user.get.id.providerId must_== UsernamePasswordProvider.UsernamePassword
      user.get.firstName must_== testUser.firstName
      user.get.lastName must_== testUser.lastName
      user.get.fullName must_== "John Rambo"
      user.get.email.get must_== testUser.email.get
      user.get.avatarUrl must_== None
      user.get.authMethod must_== AuthenticationMethod.UserPassword
      user.get.oAuth1Info must_== None
      user.get.oAuth2Info must_== None
      user.get.passwordInfo.get.hasher must_== testUser.passwordInfo.get.hasher
      user.get.passwordInfo.get.password must_== testUser.passwordInfo.get.password
      user.get.passwordInfo.get.salt.get must_== testUser.passwordInfo.get.salt.get
    }
  }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}