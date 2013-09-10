import play.api._
import play.api.mvc._
import play.api.http.HeaderNames._
import play.extras.iteratees.GzipFilter

object Global extends WithFilters(new GzipFilter()) {

  override def onStart(app: Application) {
    Logger.info("Anechoic has started")
  }

  override def onStop(app: Application) {
    Logger.info("Anechoic shutdown...")
  }
}