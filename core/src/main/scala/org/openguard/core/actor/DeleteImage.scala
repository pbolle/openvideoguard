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
import scala.reflect.io.File

/**
 * Created by pbolle on 20.06.15.
 */
class DeleteImage extends Actor {
  def imageRefDAO = new ImageRefDAO

  var homeDir = Play.configuration.getString("ftp.homedirectory").getOrElse("~/")
  val DROP_RATE: Int = 2;
  val MAX_EVENS: Int = 20
  val DELETE_AFTER_DAYS: Int = 14

  def receive: Receive = {

    case photo: DeleteRule => {
      val startTime = LocalDateTime.now.minusDays(DELETE_AFTER_DAYS).atZone(ZoneId.systemDefault()).toEpochSecond * 1000
      var framesFuture = imageRefDAO.selectDeleteFrames(IMAGE, new Timestamp(startTime), MAX_EVENS)

      for {
        frames <- framesFuture
      } yield {
        //works
        //frame.foreach(println)
        frames.map(imageRefDAO.findAllInFrame(_)).map(filterEventsInFrame)
      }

    }
  }

  def filterEventsInFrame(eventsFuture: Future[List[ImageRef]]) = {
    for {
      events <- eventsFuture
    } yield {
      for (event <- events.grouped(DROP_RATE).map(_.head)) {
        imageRefDAO.delete(event.imgPath)
        println(homeDir + event.thumbnailPath)
        deleteFile(event.thumbnailPath)
        deleteFile(event.imgPath)
      }
    }
  }

  def deleteFile(filepath: String): AnyVal = {
    var file = File(homeDir + filepath)
    if (file.exists) {
      file.delete()
    }
  }

}
