package controllers

import java.sql.Timestamp

import org.openguard.core.dao.ImageRefDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.openguard.core.models._
import play.api.mvc.{Action, Controller}

class EventController extends Controller {
  private val PAGE_SIZE = 18
  val formatDay = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
  def imageRefDAO = new ImageRefDAO

  def index(camera: String = ALL_CAMERAS,mediatype : String = ALL_MEDIA ,page: Int = 1) = Action.async {
    val recentImagesFuture = imageRefDAO.recent(new Timestamp(System.currentTimeMillis()), mediatype, page, PAGE_SIZE)
    val countFuture = imageRefDAO.count(mediatype)

    for {
      recentImages <- recentImagesFuture
      count <- countFuture
    } yield {
      Ok(views.html.index(formatDay.format(System.currentTimeMillis()), recentImages, count, mediatype, page, PAGE_SIZE))
    }
  }

  def fromDateIndex(time: String,camera: String = ALL_CAMERAS,mediatype : String = ALL_MEDIA ,page: Int = 1) = Action.async {
    val recentImagesFuture = imageRefDAO.recent(new Timestamp(formatDay.parse(time).getTime),mediatype, page, PAGE_SIZE)
    val countFuture = imageRefDAO.count(mediatype)
    for {
      recentImages <- recentImagesFuture
      count <- countFuture
    } yield {
      Ok(views.html.index(time,recentImages, count, mediatype, page, PAGE_SIZE))
    }
  }

}
