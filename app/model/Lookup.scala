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
    1 -> "feed"
  )

  val WidgetKind_Name_Id = Map(
    "feed" -> 1
  )
}
