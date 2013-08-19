package model

import java.net.URL
import org.joda.time.DateTime

case class Item (
  title: String,
  description: String,
  link: URL,
  date: DateTime,
  author: String
)

case class Feed (
  title: String,
  description: String,
  link: URL,
  date: DateTime,
  items: Seq[Item]
)