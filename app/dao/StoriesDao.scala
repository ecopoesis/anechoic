package dao

import model.Story
import play.api.Play.current
import play.api.db.DB
import anorm._

object StoriesDao {

  def get(page: Int, size: Int): List[Story] = {
    DB.withConnection { implicit c =>
      val rs = SQL(
        """
          |select
          | id, title, url, score
          |from stories
        """.stripMargin)
        rs().map(
          story => new Story(story[Int]("id"), story[String]("title"), story[String]("url"), story[Int]("score"))
        ).toList
    }
  }
}
