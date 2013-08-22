package dao

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import scala.Some
import org.joda.time.DateTime
import play.api.Play.current
import AnormExtension._
import model.Widget
import model.Lookup._

object WidgetDao {
  val widget: RowParser[Widget] = {
    get[Long]("id") ~
    get[Long]("user_id") ~
    get[Int]("type") ~
    get[DateTime]("created_at") map {
    case id ~ user_id ~ kind ~ created_at =>
      Widget(
        id,
        user_id,
        WidgetKind_Id_Sym(kind),
        created_at,
        selectProperties(id)
      )
    }
  }

  def select(id: Long): Option[Widget] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, user_id, type, created_at
          |from widgets
          |where id = {id}
        """.stripMargin)
        .on('id -> id)
        .singleOpt(widget)
    }
  }

  private def selectProperties(id: Long): Map[Symbol, String] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | k, v
          |from widget_properties
          |where id = {id}
        """.stripMargin)
        .on('id -> id)
        .as({
          get[String]("k") ~
          get[String]("v")
        }.*)
        .map { case k ~ v => (Symbol(k), v) }.toMap
    }
  }

  def insert(userId: Long, t: Symbol): Any = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into widgets (user_id, type) values ({user_id}, {type})
        """.stripMargin)
        .on(
        'user_id -> userId,
        'type -> WidgetKind_Sym_Id(t)
        )
        .executeInsert() match {
        case Some(a) => a
        case None => None
      }
    }
  }

  def insertProperty(widgetId: Long, key: String, value: String) {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into widget_properties (widget_id, k, v) values ({widget_id}, {k}, {v})
        """.stripMargin)
        .on(
        'widget_id -> widgetId,
        'k -> key,
        'v -> value
      )
        .executeInsert() match {
        case Some(a) => a
        case None => None
      }
    }
  }
}
