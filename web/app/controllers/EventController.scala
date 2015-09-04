package controllers

import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.openguard.core.models._
import play.api.mvc.{Action, Controller}

class EventController extends Controller {
  private val PAGE_SIZE = 18

  def imageRefDAO = new ImageRefDAO

  def index(camera: String = ALL_CAMERAS,mediatype : String = ALL_MEDIA ,page: Int = 1) = Action.async {
    val recentImagesFuture = imageRefDAO.recent(mediatype, page, PAGE_SIZE)
    val countFuture = imageRefDAO.count(mediatype)

    for {
      recentImages <- recentImagesFuture
      count <- countFuture
    } yield {
      Ok(views.html.index(recentImages, count, mediatype, page, PAGE_SIZE))
    }
  }

}
