package dao

import model.User
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current

import securesocial.core.{Identity, PasswordInfo, AuthenticationMethod, UserId}
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

  def getById(id: Long): Option[User] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, username, password, pw_hash, pw_salt, firstname, lastname, email
          |from users
          |where id = {id}
        """.stripMargin)
        .on('id -> id)
        .singleOpt(user)
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
        .on('username -> username)
        .singleOpt(user)
    }
  }

  def getByEmail(email: String): Option[User] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, username, password, pw_hash, pw_salt, firstname, lastname, email
          |from users
          |where email = {email}
        """.stripMargin)
        .on('email -> email)
        .singleOpt(user)
    }
  }

  /**
   * create a user if they don't exist, otherwise update
   */
  def upsert(identity: Identity): Option[Identity] = {
    getByUsername(identity.id.id) match {
      case Some(_) => update(identity)
      case None => insert(identity)
    }

    getByUsername(identity.id.id)
  }

  def insert(identity: Identity): Any = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into users (username, password, pw_hash, pw_salt, firstname, lastname, email) values ({username}, {password}, {pw_hash}, {pw_salt}, {firstname}, {lastname}, {email})
        """.stripMargin)
        .on(
          'username -> identity.id.id,
          'password -> identity.passwordInfo.get.password,
          'pw_hash -> identity.passwordInfo.get.hasher,
          'pw_salt -> identity.passwordInfo.get.salt,
          'firstname -> identity.firstName,
          'lastname -> identity.lastName,
          'email -> identity.email
      )
      .executeInsert() match {
        case Some(a) => a
        case None => None
      }
    }
  }

  def update(identity: Identity) {
    DB.withConnection { implicit c =>
      SQL(
        """
          |update users set password={password}, pw_hash={pw_hash}, pw_salt={pw_salt}, firstname={firstname}, lastname={lastname}, email={email} where username = {username}
        """.stripMargin)
        .on(
          'username -> identity.id.id,
          'password -> identity.passwordInfo.get.password,
          'pw_hash -> identity.passwordInfo.get.hasher,
          'pw_salt -> identity.passwordInfo.get.salt,
          'firstname -> identity.firstName,
          'lastname -> identity.lastName,
          'email -> identity.email
      )
      .executeUpdate()
    }
  }
}
