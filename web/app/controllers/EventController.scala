package controllers

import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

class EventController extends Controller {
  private val PAGE_SIZE = 18

  def imageRefDAO = new ImageRefDAO

  def index(page: Int) = Action.async {
    val recentImagesFuture = imageRefDAO.recent(page, PAGE_SIZE)
    val countFuture = imageRefDAO.count()

    for {
      recentImages <- recentImagesFuture
      count <- countFuture
    } yield {
      Ok(views.html.index(recentImages, count, page, PAGE_SIZE))
    }
  }

}
