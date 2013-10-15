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
    3 -> "welcome",
    4 -> "stock",
    5 -> "mail",
    6 -> "calendar"
  )

  val WidgetKind_Name_Id = Map(
    "feed" -> 1,
    "weather" -> 2,
    "welcome" -> 3,
    "stock" -> 4,
    "mail" -> 5,
    "calendar" -> 6
  )
}
