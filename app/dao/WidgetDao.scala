package dao

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import scala.Some
import org.joda.time.DateTime
import play.api.Play.current
import AnormExtension._
import model.{User, Widget}
import model.Lookup._
import java.sql.Connection
import controllers.Dashboard.WidgetLocation

object WidgetDao {
  val widget: RowParser[Widget] = {
    get[Long]("id") ~
    get[Long]("user_id") ~
    get[Int]("type") ~
    get[Option[Int]]("col") ~
    get[Option[Int]]("pos") ~
    get[DateTime]("created_at") map {
    case id ~ user_id ~ kind ~ column ~ position ~ created_at =>
      Widget(
        id,
        user_id,
        WidgetKind_Id_Name(kind),
        column,
        position,
        created_at,
        collection.mutable.Map() ++ selectProperties(id)
      )
    }
  }

  /**
   * @todo make this use transactions - for some reason execute only returns false, so I can't use it for transactions
   */
  def saveLayout(user: User, locations: List[WidgetLocation]): Boolean = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |delete from widget_layout where widget_id in (select id from widgets where user_id = {user_id})
        """.stripMargin)
        .on('user_id -> user.numId)
        .execute

      for(location <- locations) {
        SQL(
          """
            |insert into widget_layout
            |(widget_id, col, pos)
            |values
            |({widget}, {column}, {position})
          """.stripMargin)
          .on(
            'widget -> location.widget,
            'column -> location.column,
            'position -> location.position
          )
          .execute()
      }
    }
    true
  }

  private def setPosition(widgetId: Long, col: Int, pos: Int) {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into widget_layout
          |(widget_id, col, pos)
          |values
          |({widget}, {column}, {position})
        """.stripMargin)
        .on(
        'widget -> widgetId,
        'column -> col,
        'position -> pos
      )
      .execute()
    }
  }

  def getAll(userId: Long): Seq[Widget] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | widgets.id, user_id, type, col, pos, widgets.created_at
          |from widgets
          |left join widget_layout on widget_layout.widget_id = widgets.id
          |where user_id = {user_id}
          |order by
          | coalesce(col, -1) ASC,
          | coalesce(pos, -1) ASC,
          | widgets.id ASC
        """.stripMargin)
        .on('user_id -> userId)
        .as(widget.*)
    }
  }

  def select(id: Long): Option[Widget] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | widgets.id, user_id, type, col, pos, widgets.created_at
          |from widgets
          |left join widget_layout on widget_layout.widget_id = widgets.id
          |where widgets.id = {id}
        """.stripMargin)
        .on('id -> id)
        .singleOpt(widget)
    }
  }

  private def selectProperties(id: Long): Map[String, String] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | k, v
          |from widget_properties
          |where widget_id = {id}
        """.stripMargin)
        .on('id -> id)
        .as({
          get[String]("k") ~
          get[String]("v")
        }.*)
        .map { case k ~ v => (k, v) }.toMap
    }
  }

  private def insertWidget(implicit c: Connection, userId: Long, t: String): Option[Long] = {
    SQL(
      """
        |insert into widgets (user_id, type) values ({user_id}, {type})
      """.stripMargin)
      .on(
      'user_id -> userId,
      'type -> WidgetKind_Name_Id(t)
      )
      .executeInsert()
  }

  private def insertProperty(implicit c: Connection, widgetId: Long, key: String, value: String): Boolean = {
    val result = SQL(
      """
        |insert into widget_properties (widget_id, k, v) values ({widget_id}, {k}, {v})
      """.stripMargin)
      .on(
      'widget_id -> widgetId,
      'k -> key,
      'v -> value
      )
      .executeInsert()

    result match {
      case Some(a) => true
      case None => false
    }
  }

  /**
   * @todo make this use transactions - for some reason execute only returns false, so I can't use it for transactions
   */
  def delete(widgetId: Long): Boolean = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |delete from widget_layout where widget_id = {widget_id}
        """.stripMargin)
        .on('widget_id -> widgetId)
        .execute

      SQL(
        """
          |delete from widget_properties where widget_id = {widget_id}
        """.stripMargin)
        .on('widget_id -> widgetId)
        .execute

      SQL(
        """
          |delete from widgets where id = {widget_id}
        """.stripMargin)
        .on('widget_id -> widgetId)
        .execute
    }

    true
  }

  def addFeed(user: User, url: String, max: Int): Long = {
    DB.withTransaction { implicit c =>
      insertWidget(c, user.numId, "feed") match {
        case Some(widgetId) => {
          if (
            insertProperty(c, widgetId, "url", url) &&
            insertProperty(c, widgetId, "max", max.toString)) {

            c.commit()
            widgetId
          } else {
            c.rollback
            0
          }
        }
        case _ => {
          c.rollback
          0
        }
      }
    }
  }

  def addWeather(user: User, city: String, wunderId: String): Long = {
    DB.withTransaction { implicit c =>
      insertWidget(c, user.numId, "weather") match {
        case Some(widgetId) => {
          if (
            insertProperty(c, widgetId, "city", city) &&
            insertProperty(c, widgetId, "wunderId", wunderId)) {

            c.commit()
            widgetId
          } else {
            c.rollback
            0
          }
        }
        case _ => {
          c.rollback
          0
        }
      }
    }
  }

  def addWelcome(user: User): Long = {
    DB.withTransaction { implicit c =>
      insertWidget(c, user.numId, "welcome") match {
        case Some(widgetId) => {
          c.commit()
          widgetId
        }
        case _ => {
          c.rollback
          0
        }
      }
    }
  }

  def duplicateSystem(user: User) = {
    val widgets = WidgetDao.getAll(UserDao.getByUsername("system").get.numId)
    for (widget <- widgets) {
      widget.kind match {
        case "feed" => {
          val widgetId = addFeed(user, widget.properties.get("url").get, widget.properties.get("max").get.toInt)
          setPosition(widgetId, widget.column.getOrElse(-1), widget.position.getOrElse(-1))
        }
        case "weather" => {
          val widgetId = addWeather(user, widget.properties.get("city").get, widget.properties.get("wunderId").get)
          setPosition(widgetId, widget.column.getOrElse(-1), widget.position.getOrElse(-1))
        }
        case "welcome" => {
          val widgetId = addWelcome(user)
          setPosition(widgetId, widget.column.getOrElse(-1), widget.position.getOrElse(-1))
        }
        case _ => {}
      }
    }
  }
}
