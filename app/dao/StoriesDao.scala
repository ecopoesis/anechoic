package dao

import model.Story
import play.api.Play.current
import play.api.db.DB
import anorm._

object StoriesDao {

  def DEFAULT_SCORE = 0

  def get(page: Int, size: Int): List[Story] = {
    DB.withConnection { implicit c =>
      val rs = SQL(
        """
          |select
          | id, title, url, score
          |from stories
        """.stripMargin)
        rs().map(
          story => new Story(story[Long]("id"), story[String]("title"), story[String]("url"), story[Int]("score"))
        ).toList
    }
  }

  def add(title: String, url: String): Any = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into stories (title, url, score) values ({title}, {url}, {score})
        """.stripMargin)
      .on("title" -> title, "url" -> url, "score" -> DEFAULT_SCORE)
      .executeInsert() match {
        case Some(a) => a
        case None => None
      }
    }
  }
}
