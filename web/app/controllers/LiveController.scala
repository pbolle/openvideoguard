package controllers

import java.sql.Timestamp

import org.openguard.core.actor.UpdateWebSocketActor
import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee._
import play.api.mvc.{Action, Controller, _}

import scala.concurrent.duration._
import scala.util.Random

class LiveController extends Controller {
  def imageRefDAO = new ImageRefDAO

  def index() = Action.async {
    implicit request =>
      val recentImagesFuture = imageRefDAO.recent(new Timestamp(System.currentTimeMillis()), IMAGE, 1, 1)
      for {
        recentImages <- recentImagesFuture
      } yield {
        Ok(views.html.live(recentImages.head))
      }
  }

  def update = WebSocket.acceptWithActor[String, String] {
    request => out => UpdateWebSocketActor.register(out)
  }

/*
  def update2 = WebSocket.using[String] {
    request =>
      val in = Iteratee.ignore[String]
      val out = Enumerator.repeatM {
        Promise.timeout(getLoadAverage, 3 seconds)
      }
      (in, out)
  }

  def getLoadAverage: String = {
    Random.nextInt(100) + "%"
  }
*/
}