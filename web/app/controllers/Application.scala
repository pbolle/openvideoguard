package controllers

import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

class Application extends Controller {
  def imageRefDAO = new ImageRefDAO

  def index(page: Option[Int]) = Action.async {
    val recentImagesFuture = imageRefDAO.recent(page.getOrElse(0))
    val countFuture = imageRefDAO.count()

    for {
      recentImages <- recentImagesFuture
      count <- countFuture
    } yield {
      Ok(views.html.index(recentImages, count))
    }
  }

}
