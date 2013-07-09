package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.UserId
import scala.Some
import org.mindrot.jbcrypt.BCrypt


/**
 * maps SecureSocial's users to our user objects
 */
class UserService(application: Application) extends UserServicePlugin(application) {
  private var users = Map[String, Identity]()
  private var tokens = Map[String, Token]()

  /**
   * finds a user that matches the specified id
   * @param id the user id
   * @return an optional user
   */
  def find(id: UserId): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    users.get(id.id + id.providerId)
  }

  /**
   * Finds a user by email and provider id.
   * @param email - the user email
   * @param providerId - the provider id
   * @return
   */
  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    users.values.find( u => u.email.map( e => e == email && u.id.providerId == providerId).getOrElse(false))
  }

  /**
   * Saves the user.  This method gets called when a user logs in.
   * This is your chance to save the user information in your backing store.
   * @param user the user to save
   */
  def save(user: Identity): Identity = {
    users = users + (user.id.id + user.id.providerId -> user)
    // this sample returns the same user object, but you could return an instance of your own class
    // here as long as it implements the Identity trait. This will allow you to use your own class in the protected
    // actions and event callbacks. The same goes for the find(id: UserId) method.
    user
  }

  /**
   * Saves a token.  This is needed for users that
   * are creating an account in the system instead of using one in a 3rd party system.
   * @param token The token to save
   * @return A string with a uuid that will be embedded in the welcome email.
   */
  def save(token: Token) {
    tokens += (token.uuid -> token)
  }

  /**
   * Finds a token
   * @param token the token id
   * @return
   */
  def findToken(token: String): Option[Token] = {
    tokens.get(token)
  }

  /**
   * Deletes a token
   * @param uuid the token id
   */
  def deleteToken(uuid: String) {
    tokens -= uuid
  }

  def deleteTokens() {
    tokens = Map()
  }

  /**
   * Deletes all expired tokens
   */
  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }
}