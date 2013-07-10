package dao

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import securesocial.core.providers.Token
import org.joda.time.DateTime

class TokenDaoSpec extends Specification {

  // wipe out millsOfSecond because I can't get Postgres to not nuke it

  val testToken = new Token(
    "xxx",
    "test@example.com",
    new DateTime().withMillisOfSecond(0),
    new DateTime().plusDays(1).withMillisOfSecond(0),
    false
  )

  val expiredToken = new Token(
    "yyy",
    "expired@example.com",
    new DateTime().minusDays(2).withMillisOfSecond(0),
    new DateTime().minusDays(1).withMillisOfSecond(0),
    false
  )

  "TokenDao" should {
      "token 'xxx' shouldn't exist" in testDb {
        TokenDao.select("xxx") must_== None
      }

      "insert a token" in testDb {
        TokenDao.select(testToken.uuid) must_== None
        TokenDao.insert(testToken)
        val token = TokenDao.select(testToken.uuid)
        token.get.uuid must_== testToken.uuid
        token.get.email must_== testToken.email
        token.get.creationTime must_== testToken.creationTime
        token.get.expirationTime must_== testToken.expirationTime
        token.get.isSignUp must_== testToken.isSignUp
      }

      "delete a token" in testDb {
        TokenDao.select(testToken.uuid) must_== None
        TokenDao.insert(testToken)
        val token = TokenDao.select(testToken.uuid)
        token.get.uuid must_== testToken.uuid
        token.get.email must_== testToken.email
        token.get.creationTime must_== testToken.creationTime
        token.get.expirationTime must_== testToken.expirationTime
        token.get.isSignUp must_== testToken.isSignUp
        TokenDao.delete(testToken.uuid)
        TokenDao.select(testToken.uuid) must_== None
      }

      "delete expired tokens" in testDb {
        TokenDao.select(testToken.uuid) must_== None
        TokenDao.select(expiredToken.uuid) must_== None
        TokenDao.insert(testToken)
        TokenDao.insert(expiredToken)
        TokenDao.select(testToken.uuid).get.uuid must_== testToken.uuid
        TokenDao.select(expiredToken.uuid).get.uuid must_== expiredToken.uuid
        TokenDao.deleteExpired()
        TokenDao.select(testToken.uuid).get.uuid must_== testToken.uuid
        TokenDao.select(expiredToken.uuid) must_== None
      }
    }

  def testDb[T](code: =>T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url"    -> "jdbc:h2:mem:anechoic;MODE=PostgreSQL"
    )))(code)
}