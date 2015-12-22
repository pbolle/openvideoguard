package org.openguard.core.actor

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneId}

import akka.actor.Actor
import org.openguard.core.DeleteRule
import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models._
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import java.io.File

/**
 * Created by pbolle on 20.06.15.
 */
class DeleteImage extends Actor {
  def imageRefDAO = new ImageRefDAO

  var homeDir = Play.configuration.getString("ovg.publicDirectory").getOrElse("~/")
  val DROP_RATE: Int = 2;

  def receive: Receive = {

    case deleteRule: DeleteRule => {
      val startTime = LocalDateTime.now.minusDays(deleteRule.delteAfterDays).atZone(ZoneId.systemDefault()).toEpochSecond * 1000
      var framesFuture = imageRefDAO.selectDeleteFrames(deleteRule.mediaType, new Timestamp(startTime), deleteRule.maxEvents)

      for {
        frames <- framesFuture
      } yield {
        frames.map(imageRefDAO.findAllInFrame(_)).map(filterEventsInFrame(_,deleteRule.dropRate))
      }

    }
  }

  def filterEventsInFrame(eventsFuture: Future[List[ImageRef]],dropRate: Int) = {
    for {
      events <- eventsFuture
    } yield {
      for (event <- events.grouped(dropRate).map(_.head)) {
        imageRefDAO.delete(event.imgPath)
        deleteFile(event.thumbnailPath)
        deleteFile(event.imgPath)
      }
    }
  }

  def deleteFile(filepath: String): AnyVal = {
    var file = new File(homeDir + File.separator + filepath)
    if (file.exists) {
      file.delete()
    }
    if (file.getParentFile.listFiles().length == 0){
      file.getParentFile.delete();
    }
  }

}
