import play.api._
import play.api.mvc._
import play.api.http.HeaderNames._
import play.filters.gzip.GzipFilter
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current

object Global extends WithFilters(new GzipFilter()) with GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Anechoic has started")
  }

  override def onStop(app: Application) {
    Logger.info("Anechoic shutdown...")
  }

  override def doFilter(action: EssentialAction): EssentialAction = EssentialAction { request =>
    if (Play.isProd) {
      if (request.path.contains(".") && request.path.contains("assets")) {
        // in prod, cache items with . in their name (images, css, js etc) forever
        // also add htst
        action.apply(request).map(_.withHeaders(
          CACHE_CONTROL -> "public, max-age=2592000, no-cache",
          "Strict-Transport-Security" -> "max-age=16070400; includeSubDomains"
        ))
      } else {
        // just add hsts to everything else
        action.apply(request).map(_.withHeaders(
          "Strict-Transport-Security" -> "max-age=16070400; includeSubDomains"
        ))
      }
    } else {
      // don't cache anything else
      action.apply(request).map(_.withHeaders(
        PRAGMA -> "no-cache",
        CACHE_CONTROL -> "private, max-age=0, no-cache"
      ))
    }
  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if (Play.isProd && !request.headers.get("x-forwarded-proto").getOrElse("").contains("https")) {
      Some(controllers.Secure.redirect)
    } else {
      super.onRouteRequest(request)
    }
  }
}