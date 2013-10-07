package dao

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.Play.current
import AnormExtension._
import scala.language.postfixOps
import model._
import scala.collection.mutable
import anorm.~
import model.Comment
import model.CommentHierarchy
import scala.Some

object CommentDao {
  val DefaultScore = 0

  val commentParser: RowParser[Comment] = {
    get[Long]("id") ~
    get[Long]("user_id") ~
    get[Long]("story_id") ~
    get[Option[Long]]("parent_id") ~
    get[String]("comment") ~
    get[Int]("score") ~
    get[DateTime]("created_at") map {
    case id ~ user_id ~ story_id ~ parent_id ~ comment ~ score ~ created_at =>
      Comment(
        id,
        UserDao.getById(user_id).get,
        StoryDao.getId(story_id).get,
        parent_id,
        comment,
        score,
        created_at,
        new mutable.TreeSet[Comment]()
      )
    }
  }

  /**
   * @todo there should be a way to get the 'all' map directly from anorm so we don't have to waster effort sorting it in the DB or building the map from the list
   */
  def getComments(storyId: Long): CommentHierarchy = {
    val comments = DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, user_id, story_id, parent_id, comment, score, created_at
          |from comments
          |where story_id = {story_id}
          |order by id
        """.stripMargin)
        .on('story_id -> storyId)
        .as(commentParser *)
    }

    val hierarchy = new CommentHierarchy(storyId, new mutable.TreeSet[Comment]())

    // map of all comments so we can easily find where to put them in the hierarchy
    val all = new mutable.HashMap[Long, Comment]()

    for(comment <- comments) {
      // add to master comments map
      all.put(comment.id, comment)

      comment.parentId match {
        // if we have a parent ID, find that comment and this comment as a child
        case Some(parentId: Long) => all.get(parentId).get.children.add(comment)

        // if we have no parent, add to the root comments
        case None => hierarchy.root.add(comment)
      }
    }

    hierarchy
  }

  def getComment(commentId: Long): Option[Comment] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select id, user_id, story_id, parent_id, comment, score, created_at
          |from comments
          |where id = {id}
        """.stripMargin)
        .on('id -> commentId)
        .singleOpt(commentParser)
    }
  }

  def add(storyId: Long, parentId: Option[Long], text: String, userId: Long): Any = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |insert into comments (user_id, story_id, parent_id, comment, score) values ({user_id}, {story_id}, {parent_id}, {comment}, {score})
        """.stripMargin)
        .on(
          'user_id -> userId,
          'story_id -> storyId,
          'parent_id -> parentId,
          'comment -> text,
          'score -> DefaultScore
      )
        .executeInsert() match {
          case Some(a) => a
          case None => None
      }
    }
  }

  def voted(commentId: Long, userId: Long): Boolean = {
    DB.withConnection { implicit c =>
      val row = SQL(
        """
          |select case when count(*) > 0 then true else false end as voted from audit where type_id={type_id} and action_id={action_id} and object_id={comment_id} and user_id={user_id}
        """.stripMargin)
        .on(
          'type_id -> Lookup.AuditType.get('comment),
          'action_id -> Lookup.AuditAction.get('vote),
          'comment_id -> commentId,
          'user_id -> userId
      ).single

      row[Boolean]("voted")
    }
  }

  def vote(commentId: Long, userId: Long, delta: Int): Boolean = {
    DB.withTransaction { implicit c =>
      SQL(
        """
          |update comments set score = score + {delta} where id = {commentId}
        """.stripMargin)
        .on(
        'commentId -> commentId,
        'delta -> delta
      ).executeUpdate() match {
        case 1 => {
          SQL(
            """
              |insert into audit (type_id, action_id, object_id, value, user_id) values ({type_id}, {action_id}, {comment_id}, {value}, {user_id})
            """.stripMargin)
            .on(
            'type_id -> Lookup.AuditType.get('comment),
            'action_id -> Lookup.AuditAction.get('vote),
            'comment_id -> commentId,
            'value -> delta,
            'user_id -> userId
          )
            .executeInsert() match {
            case Some(a) => {
              return true
            }
            case None => {
              return false
            }
          }
        }
        case _ => {
          return false
        }
      }
    }
  }
}
