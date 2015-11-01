package controllers

import org.openguard.core.dao.ImageRefDAO
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.openguard.core.models._

class CalendarController extends Controller {
  def imageRefDAO = new ImageRefDAO

  def index() = Action.async {
    val imagesPerHourFuture = imageRefDAO.selectEventsPerHour(IMAGE);
    val videosPerHourFuture = imageRefDAO.selectEventsPerHour(VIDEO);
    for {
      imagesPerHour <- imagesPerHourFuture
      videosPerHour <- videosPerHourFuture
    } yield {
      Ok(views.html.calendar(imagesPerHour, videosPerHour))
    }
  }
}
