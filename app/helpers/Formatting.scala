package helpers

import org.joda.time.DateTime
import org.ocpsoft.prettytime.PrettyTime

object Formatting {

  def print(dt: DateTime): String = {
    val pt = new PrettyTime()
    pt.format(dt.toDate)
  }
}
