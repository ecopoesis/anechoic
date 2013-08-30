package helpers

import play.api.Play
import play.api.Play.current

object Web {
  def include(filename: String): String = {
    val source = scala.io.Source.fromURL((Play.classloader.getResource(filename)))
    source.mkString
  }
}
