package model

import org.joda.time.DateTime
import scala.collection.mutable

case class Comment(
  id: Long,
  user: User,
  story: Story,
  parentId: Option[Long],
  text: String,
  score: Int,
  createdAt: DateTime,

  // children of this comment
  children: mutable.SortedSet[Comment]
) extends Ordered[Comment] {
  /**
   * higher scores first, if tied, older first
   */
  def compare(that: Comment) = {
    if (this.score == that.score) {
      this.createdAt compareTo that.createdAt
    } else {
      this.score compareTo that.score
    }
  }
}

case class CommentHierarchy(
  storyId: Long,

  // the root level comments
  root: mutable.SortedSet[Comment]
)