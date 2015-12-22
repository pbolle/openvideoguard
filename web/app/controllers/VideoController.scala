package controllers

import play.api.mvc.{Action, Controller}

class VideoController extends Controller {

   def index(path: String)  = Action {
     Ok(views.html.video(path))
   }
}
