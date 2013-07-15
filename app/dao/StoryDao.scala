package dao

import model.Story
import play.api.Play.current
import play.api.db.DB
import anorm._
import play.api.Logger
import anorm.SqlParser._
import AnormExtension._
import org.joda.time.DateTime
import scala.language.postfixOps

object StoryDao {
  val Gravity = 1.8
  val DefaultScore = 0

  val story: RowParser[Story] = {
    get[Long]("id") ~
    get[String]("title") ~
    get[String]("url") ~
    get[Int]("score") ~
    get[Long]("user_id") ~
    get[DateTime]("created_at") map {
      case id ~ title ~ url ~ score ~ user_id ~ created_at =>
        Story(
          id,
          title,
          url,
          score,
          UserDao.getById(user_id),
          created_at
        )
    }
  }

  def getId(id: Long): Option[Story] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, title, url, score, user_id, created_at
          |from stories
          |where id = {id}
        """.stripMargin)
        .on('id -> id)
        .singleOpt(story)
    }
  }

  def getList(page: Int, size: Int): List[Story] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, title, url, score, user_id, created_at
          |from stories
          |order by
          | case when score = 0 then 1 else score end / power((EXTRACT(EPOCH FROM current_timestamp - created_at) / 3600) + 2, {gravity}) desc
          |limit {size} offset ({page} - 1) * {size}
        """.stripMargin)
        .on(
          'gravity -> Gravity,
          'page -> page,
          'size -> size
        )
        .as(story *)
    }
  }

  def add(title: String, url: String, userId: Long): Any = {
    Logger.debug("title = %s, url = %s".format(title, url))
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into stories (title, url, score, user_id) values ({title}, {url}, {score}, {user_id})
        """.stripMargin)
      .on(
        'title -> title,
        'url -> url,
        'score -> DefaultScore,
        'user_id -> userId
      )
      .executeInsert() match {
        case Some(a) => a
        case None => None
      }
    }
  }
}
