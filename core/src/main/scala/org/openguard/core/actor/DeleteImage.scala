package org.openguard.core.actor

import java.sql.Timestamp
import java.time.{ZoneId, LocalDateTime}

import scala.concurrent.duration._
import akka.actor.Actor
import org.joda.time.DateTime
import org.openguard.core.DeleteRule
import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by pbolle on 20.06.15.
 */
class DeleteImage extends Actor {
  def imageRefDAO = new ImageRefDAO

  def receive: Receive = {
    case photo: DeleteRule => {
      val startTime = LocalDateTime.now.minusDays(5).atZone(ZoneId.systemDefault()).toEpochSecond * 1000
      var frames = imageRefDAO.selectDeleteFrames(IMAGE,new Timestamp(startTime))

      for(frame <- frames){
        println(frame)
      }

      //      println("ping")
    }
  }
}
