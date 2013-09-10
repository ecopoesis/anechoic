package helpers

import org.joda.time.DateTime
import org.ocpsoft.prettytime.PrettyTime
import model.Story
import scala.util.matching.Regex
import controllers.routes
import play.api.Play
import play.api.Play.current

object Formatting {

  def print(dt: DateTime): String = {
    val pt = new PrettyTime()
    pt.format(dt.toDate)
  }

  def canonicalUrl(host: String, story: Story) = {
    "//" + host + "/story/" + urlify(story.title) + "/" + story.id
  }

  def urlify(s: String) = {
    // remove everything but letters, numbers and spaces
    val remove = new Regex("[^0-9a-zA-z\\s]")

    // change whitespace to underscores
    val replace = new Regex("\\s+")

    replace replaceAllIn(remove replaceAllIn(s, ""), "_")
  }

  def asset(f: String) = {
    if (Play.isProd && (f.endsWith(".js") || f.endsWith(".css") && !f.contains(".min"))) {
      val n = f.replace(".css", ".min.css") //.replace(".js", ".min.js")
      routes.Www.asset(n, Play.current.configuration.getString("application.version").get)
    } else {
      routes.Www.asset(f, Play.current.configuration.getString("application.version").get)
    }
  }
}
