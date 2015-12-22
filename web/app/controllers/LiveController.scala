package controllers

import java.sql.Timestamp
import org.openguard.core.models._
import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

class LiveController extends Controller {
  def imageRefDAO = new ImageRefDAO
   def index()  = Action.async  {
     val recentImagesFuture = imageRefDAO.recent(new Timestamp(System.currentTimeMillis()), IMAGE, 1, 1)
     for {
       recentImages <- recentImagesFuture
     } yield {
       Ok(views.html.live(recentImages.head))
     }
   }
}
