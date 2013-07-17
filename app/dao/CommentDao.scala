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
import model.User
import model.Comment
import model.CommentHierarchy
import scala.Some

case class RawComment(
  id: Long,
  user: User,
  storyId: Long,
  parentId: Option[Long],
  text: String,
  score: Int,
  createdAt: DateTime
)

object CommentDao {
  val DefaultScore = 0

  val rawComment: RowParser[RawComment] = {
    get[Long]("id") ~
    get[Long]("user_id") ~
    get[Long]("story_id") ~
    get[Option[Long]]("parent_id") ~
    get[String]("comment") ~
    get[Int]("score") ~
    get[DateTime]("created_at") map {
    case id ~ user_id ~ story_id ~ parent_id ~ comment ~ score ~ created_at =>
      RawComment(
        id,
        UserDao.getById(user_id).get,
        story_id,
        parent_id,
        comment,
        score,
        created_at
      )
    }
  }

  /**
   * @todo there should be a way to get the 'all' map directly from anorm so we don't have to waster effort sorting it in the DB or building the map from the list
   */
  def getComments(storyId: Long): CommentHierarchy = {
    val raw = DB.withConnection { implicit c =>
      SQL(
        """
          |select
          | id, user_id, story_id, parent_id, comment, score, created_at
          |from comments
          |where story_id = {story_id}
          |order by id
        """.stripMargin)
        .on('story_id -> storyId)
        .as(rawComment *)
    }

    val comments = new CommentHierarchy(storyId, new mutable.TreeSet[Comment]()(CommentOrdering))

    // map of all comments so we can easily find where to put them in the hierarchy
    val all = new mutable.HashMap[Long, Comment]()

    for(c <- raw) {
      // create the comment
      val comment = new Comment(c.id, c.user, c.text, c.score, c.createdAt, new mutable.TreeSet[Comment]()(CommentOrdering))

      // add to master comments map
      all.put(c.id, comment)

      c.parentId match {
        // if we have a parent ID, find that comment and this comment as a child
        case Some(parentId: Long) => all.get(parentId).get.children.add(comment)

        // if we have no parent, add to the root comments
        case None => comments.root.add(comment)
      }
    }

    comments
  }

  def getComment(commentId: Long): Option[RawComment] = {
    DB.withConnection { implicit c =>
      SQL(
        """
          |select id, user_id, story_id, parent_id, comment, score, created_at
          |from comments
          |where id = {id}
        """.stripMargin)
        .on('id -> commentId)
        .singleOpt(rawComment)
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
              |insert into audit (type_id, action_id, value, user_id) values ({type_id}, {action_id}, {value}, {user_id})
            """.stripMargin)
            .on(
            'type_id -> Lookup.AuditType.get('comment),
            'action_id -> Lookup.AuditAction.get('vote),
            'value -> delta,
            'user_id -> userId
          )
            .executeInsert() match {
            case Some(a) => {
              c.commit()
              return true
            }
            case None => {
              c.rollback()
              return false
            }
          }
        }
        case _ => {
          c.rollback()
          return false
        }
      }
    }
  }
}
