package dao

import model.{User}
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current

import securesocial.core.{PasswordInfo, AuthenticationMethod, UserId}
import securesocial.core.providers.UsernamePasswordProvider

object UserDao {
  val user: RowParser[User] = {
    get[Long]("id") ~
    get[String]("username") ~
    get[String]("password") ~
    get[String]("pw_hash") ~
    get[Option[String]]("pw_salt") ~
    get[String]("firstname") ~
    get[String]("lastname") ~
    get[Option[String]]("email") map {
    case id ~ username ~ password ~ pw_hash ~ pw_salt ~ firstname ~ lastname ~ email =>
      User(
        id,
        new UserId(username, UsernamePasswordProvider.UsernamePassword),
        firstname,
        lastname,
        firstname + " " + lastname,
        email,
        None,
        AuthenticationMethod.UserPassword,
        None,
        None,
        Some[PasswordInfo](new PasswordInfo(pw_hash, password, pw_salt))
        )
    }
   }

  def getByUsername(username: String): Option[User] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, username, password, pw_hash, pw_salt, firstname, lastname, email
          |from users
          |where username = {username}
        """.stripMargin)
        .on("username" -> username)
        .singleOpt(user)
    }
  }

}
