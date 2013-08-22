package model

import org.joda.time.DateTime

case class Widget(
  id: Long,
  userId: Long,
  kind: Symbol,
  createdAt: DateTime,
  properties: Map[Symbol, String]
)