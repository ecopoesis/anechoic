import play.api._
import play.api.mvc._
import play.api.http.HeaderNames._
import play.extras.iteratees.GzipFilter
import play.api.Play.current

object Global extends WithFilters(new GzipFilter()) {

  override def onStart(app: Application) {
    Logger.info("Anechoic has started")
  }

  override def onStop(app: Application) {
    Logger.info("Anechoic shutdown...")
  }

  override def doFilter(action: EssentialAction): EssentialAction = EssentialAction { request =>
    if (Play.isProd && request.path.contains(".") && request.path.contains("assets")) {
      // in prod, cache items with . in their name (images, css, js etc) forever
      action.apply(request).map(_.withHeaders(
        CACHE_CONTROL -> "public, max-age=2592000, no-cache"
      ))
    } else {
      // don't cache anything else
      action.apply(request).map(_.withHeaders(
        PRAGMA -> "no-cache",
        CACHE_CONTROL -> "private, max-age=0, no-cache"
      ))
    }
  }
}