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

  val testUserUpdate = new User(
    -1,
    new UserId("jrambo", "junk"),
    "john",
    "rambo",
    "ignored",
    Option("rambo@rambo.com"),
    None,
    AuthenticationMethod.UserPassword,
    None,
    None,
    Option(new PasswordInfo("fakehasher", "fakehashedpassword", Option("fakesalt")))
  )

  val testUserNoSalt = new User(
    -1,
    new UserId("rbalboa", "junk"),
    "Rocky",
    "Balboa",
    "ignored",
    Option("rocky@rocky.com"),
    None,
    AuthenticationMethod.UserPassword,
    None,
    None,
    Option(new PasswordInfo("fakehasher", "fakehashedpassword", None))
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
      user.get.scheme must_== "dark"
    }

    "create a user with no salt" in testDb {
      UserDao.getByUsername("rbalboa") must_== None
      UserDao.insert(testUserNoSalt) must beEqualTo(1L)

      val user = UserDao.getByUsername(testUserNoSalt.id.id)
      user must_!= None
      user.get.numId must_== 1
      user.get.id.id must_== "rbalboa"
      user.get.id.providerId must_== UsernamePasswordProvider.UsernamePassword
      user.get.firstName must_== testUserNoSalt.firstName
      user.get.lastName must_== testUserNoSalt.lastName
      user.get.fullName must_== "Rocky Balboa"
      user.get.email.get must_== testUserNoSalt.email.get
      user.get.avatarUrl must_== None
      user.get.authMethod must_== AuthenticationMethod.UserPassword
      user.get.oAuth1Info must_== None
      user.get.oAuth2Info must_== None
      user.get.passwordInfo.get.hasher must_== testUserNoSalt.passwordInfo.get.hasher
      user.get.passwordInfo.get.password must_== testUserNoSalt.passwordInfo.get.password
      user.get.passwordInfo.get.salt must_== None

      val user2 = UserDao.getById(1L)
      user2 must_!= None
      user2.get.numId must_== 1
      user2.get.id.id must_== "rbalboa"
      user2.get.id.providerId must_== UsernamePasswordProvider.UsernamePassword
      user2.get.firstName must_== testUserNoSalt.firstName
      user2.get.lastName must_== testUserNoSalt.lastName
      user2.get.fullName must_== "Rocky Balboa"
      user2.get.email.get must_== testUserNoSalt.email.get
      user2.get.avatarUrl must_== None
      user2.get.authMethod must_== AuthenticationMethod.UserPassword
      user2.get.oAuth1Info must_== None
      user2.get.oAuth2Info must_== None
      user2.get.passwordInfo.get.hasher must_== testUserNoSalt.passwordInfo.get.hasher
      user2.get.passwordInfo.get.password must_== testUserNoSalt.passwordInfo.get.password
      user2.get.passwordInfo.get.salt must_== None
    }

    "update a user" in testDb {
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

      UserDao.update(testUserUpdate)
      val user2 = UserDao.getByUsername(testUserUpdate.id.id)
      user2 must_!= None
      user2.get.numId must_== 1
      user2.get.id.id must_== "jrambo"
      user2.get.id.providerId must_== UsernamePasswordProvider.UsernamePassword
      user2.get.firstName must_== testUserUpdate.firstName
      user2.get.lastName must_== testUserUpdate.lastName
      user2.get.fullName must_== "john rambo"
      user2.get.email.get must_== testUserUpdate.email.get
      user2.get.avatarUrl must_== None
      user2.get.authMethod must_== AuthenticationMethod.UserPassword
      user2.get.oAuth1Info must_== None
      user2.get.oAuth2Info must_== None
      user2.get.passwordInfo.get.hasher must_== testUserUpdate.passwordInfo.get.hasher
      user2.get.passwordInfo.get.password must_== testUserUpdate.passwordInfo.get.password
      user2.get.passwordInfo.get.salt.get must_== testUserUpdate.passwordInfo.get.salt.get
    }

    "upsert a user" in testDb {
      UserDao.getByUsername("jrambo") must_== None
      val u1 = UserDao.upsert(testUser)
      u1 must_!= None
      u1.get.id.id must_== "jrambo"
      u1.get.id.providerId must_== UsernamePasswordProvider.UsernamePassword
      u1.get.firstName must_== testUser.firstName
      u1.get.lastName must_== testUser.lastName
      u1.get.fullName must_== "John Rambo"
      u1.get.email.get must_== testUser.email.get
      u1.get.avatarUrl must_== None
      u1.get.authMethod must_== AuthenticationMethod.UserPassword
      u1.get.oAuth1Info must_== None
      u1.get.oAuth2Info must_== None
      u1.get.passwordInfo.get.hasher must_== testUser.passwordInfo.get.hasher
      u1.get.passwordInfo.get.password must_== testUser.passwordInfo.get.password
      u1.get.passwordInfo.get.salt.get must_== testUser.passwordInfo.get.salt.get

      val u2 = UserDao.upsert(testUser)
      u2 must_!= None
      u2.get.id.id must_== "jrambo"
      u2.get.id.providerId must_== UsernamePasswordProvider.UsernamePassword
      u2.get.firstName must_== testUser.firstName
      u2.get.lastName must_== testUser.lastName
      u2.get.fullName must_== "John Rambo"
      u2.get.email.get must_== testUser.email.get
      u2.get.avatarUrl must_== None
      u2.get.authMethod must_== AuthenticationMethod.UserPassword
      u2.get.oAuth1Info must_== None
      u2.get.oAuth2Info must_== None
      u2.get.passwordInfo.get.hasher must_== testUser.passwordInfo.get.hasher
      u2.get.passwordInfo.get.password must_== testUser.passwordInfo.get.password
      u2.get.passwordInfo.get.salt.get must_== testUser.passwordInfo.get.salt.get
    }

    "update color scheme" in testDb {
      UserDao.getByUsername("jrambo") must_== None
      val u1 = UserDao.upsert(testUser)
      u1 must_!= None
      UserDao.setScheme(1, "light") must_== true
      val u2 = UserDao.getById(1)
      u2.get.scheme must_== "light"
    }

    "update unknown color scheme" in testDb {
      UserDao.getByUsername("jrambo") must_== None
      val u1 = UserDao.upsert(testUser)
      u1 must_!= None
      UserDao.setScheme(1, "fred") must_== false
      val u2 = UserDao.getById(1)
      u2.get.scheme must_== "dark"
    }
  }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}