package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by pbolle on 25.10.15.
 */
class CalendarController extends Controller {
  def index() = Action {
    Ok(views.html.calendar())
  }
}
