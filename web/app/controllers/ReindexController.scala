package controllers

import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Result, Action, Controller}

class ReindexController extends Controller {

  def index() = Action {
    Redirect(controllers.routes.ImageController.index(1))
  }

}
