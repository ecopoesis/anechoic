package model

object Lookup {
  val AuditType = Map(
    'story -> 1,
    'comment -> 2
  )

  val AuditAction = Map(
    'vote -> 1
  )

  val WidgetKind_Id_Sym = Map(
    1 -> 'feed
  )

  val WidgetKind_Sym_Id = Map(
    'feed -> 1
  )
}
