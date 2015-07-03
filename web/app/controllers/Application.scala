package controllers

import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

class Application extends Controller  {
  def imageRefDAO = new ImageRefDAO

  def index = Action.async {
    imageRefDAO.recent().map(res => Ok(views.html.index(res.toList)))
  }

}
