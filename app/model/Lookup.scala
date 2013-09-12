package model

object Lookup {
  val AuditType = Map(
    'story -> 1,
    'comment -> 2
  )

  val AuditAction = Map(
    'vote -> 1
  )

  val WidgetKind_Id_Name = Map(
    1 -> "feed",
    2 -> "weather",
    3 -> "welcome"
  )

  val WidgetKind_Name_Id = Map(
    "feed" -> 1,
    "weather" -> 2,
    "welcome" -> 3
  )
}
