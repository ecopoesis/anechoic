package dao

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import scala.Some
import org.joda.time.DateTime
import securesocial.core.providers.Token
import play.api.Play.current
import AnormExtension._

object TokenDao {
  val token: RowParser[Token] = {
    get[String]("token") ~
    get[String]("email") ~
    get[DateTime]("creation") ~
    get[DateTime]("expiration") ~
    get[Boolean]("signed_up") map {
    case uuid ~ email ~ creation ~ expiration ~ signed_up =>
      Token(
        uuid,
        email,
        creation,
        expiration,
        signed_up
      )
    }
  }

  def select(uuid: String): Option[Token] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | token, email, creation, expiration, signed_up
          |from tokens
          |where token = {uuid}
        """.stripMargin)
        .on('uuid -> uuid)
        .singleOpt(token)
    }
  }

  def insert(token: Token): Any = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into tokens (token, email, creation, expiration, signed_up) values ({token}, {email}, {creation}, {expiration}, {signed_up})
        """.stripMargin)
        .on(
          'token -> token.uuid,
          'email -> token.email,
          'creation -> token.creationTime,
          'expiration -> token.expirationTime,
          'signed_up -> token.isSignUp
      )
        .executeInsert() match {
        case Some(a) => a
        case None => None
      }
    }
  }

  def delete(uuid: String) {
    DB.withConnection { implicit c =>
      SQL(
        """
          |delete from tokens where token = {uuid}
        """.stripMargin
      )
      .on('uuid -> uuid)
      .execute()
    }
  }

  def deleteExpired() {
    DB.withConnection { implicit c =>
      val now = new DateTime()
      SQL(
        """
          |delete from tokens where expiration <= {now}
        """.stripMargin
      )
      .on('now -> now)
      .execute
    }
  }
}
