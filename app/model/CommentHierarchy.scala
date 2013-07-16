package model

import org.joda.time.DateTime
import scala.collection.mutable

case class Comment(
  id: Long,
  user: User,
  text: String,
  score: Int,
  createdAt: DateTime,

  // children of this nest
  children: mutable.SortedSet[Comment]
)

case class CommentHierarchy(
  storyId: Long,

  // the root level comments
  root: mutable.SortedSet[Comment]
)

/**
 * higher scores first, if tied, older first
 */
object CommentOrdering extends Ordering[Comment] {
  def compare(a:Comment, b:Comment) = {
    if (a.score == b.score) {
      a.createdAt compareTo b.createdAt
    } else {
      b.score compareTo a.score
    }
  }
}
