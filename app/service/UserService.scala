package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId
import scala.Some
import org.mindrot.jbcrypt.BCrypt
import dao.{TokenDao, UserDao}
import securesocial.core.Identity
import model.User


/**
 * maps SecureSocial's users to our user objects
 * we currently only support userpass users, so everything gets hardcoded to that
 * this would be easy to change in the future if we need to
 */
class UserService(application: Application) extends UserServicePlugin(application) {
  /**
   * finds a user that matches the specified id
   * @param id the user id
   * @return an optional user
   */
  def find(id: IdentityId): Option[User] = {
    UserDao.getByUsername(id.userId)
  }

  /**
   * Finds a user by email and provider id.
   * @param email - the user email
   * @param providerId - the provider id
   * @return
   */
  def findByEmailAndProvider(email: String, providerId: String): Option[User] = {
    UserDao.getByEmail(email)
  }

  /**
   * Saves the user. This method gets called when a user logs in.
   * This is your chance to save the user information in your backing store.
   * @param user the user to save
   */
  def save(user: Identity): Identity = {
    UserDao.upsert(user) match {
      case Some(a) => a
      case None => throw new Exception("error saving user")
    }
  }

  /**
   * Saves a token.  This is needed for users that
   * are creating an account in the system instead of using one in a 3rd party system.
   * @param token The token to save
   */
  def save(token: Token) {
    TokenDao.insert(token)
  }

  /**
   * Finds a token
   * @param token the token id
   * @return
   */
  def findToken(token: String): Option[Token] = {
    TokenDao.select(token)
  }

  /**
   * Deletes a token
   */
  def deleteToken(uuid: String) {
    TokenDao.delete(uuid)
  }

  /**
   * Deletes all expired tokens
   */
  def deleteExpiredTokens() {
    TokenDao.deleteExpired()
  }
}