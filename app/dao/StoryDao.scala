package dao

import model.Story
import play.api.Play.current
import play.api.db.DB
import anorm._
import play.api.Logger

object StoryDao {

  def DEFAULT_SCORE = 0

  // @todo convert to row parser
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
    Logger.debug("title = %s, url = %s".format(title, url))
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
