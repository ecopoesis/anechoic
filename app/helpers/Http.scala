package helpers

import play.api.Play.current
import play.api.Play
import dispatch._
import Defaults._

object Http {
  val userAgent = "Anechoic News v" + Play.current.configuration.getString("application.version").get + " - www.anechoicnews.com"
  val http = dispatch.Http.configure(_ setFollowRedirects true)

  def get(uri: String): String = {
    val svc = url(uri) <:< Map("User-Agent" -> userAgent)
    val f = http(svc OK as.String)
    f()
  }
}
