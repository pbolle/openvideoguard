package org.openguard.core.actor

import java.io.File
import java.time.{LocalDate, LocalTime}

import akka.actor.{Actor, Props}
import com.sksamuel.scrimage.Image
import org.openguard.core.Photo
import play.api.Play
import play.api.Play.current

/**
 * Created by pbolle on 19.06.15.
 */
class LoadImage extends Actor {

  val thumbnailWidth: Int = Play.configuration.getInt("ovg.img.thumbnail.width").getOrElse(256)
  val thumbnailHeight: Int = Play.configuration.getInt("ovg.img.thumbnail.heigth").getOrElse(144)
  var homeDir = Play.configuration.getString("ftp.homedirectory").getOrElse("~/")

  def receive = {
    case imagePath: String => {
      val image = Image.apply(new File(imagePath))
      println("image " + image)
      // crate image object with cam + time + name
      val photo = new Photo(image, imagePath)
      // move to archive
      var archiveActor = context.actorOf(Props[Archive])
      archiveActor ! photo
      // create thumbnail
      val thumbnailImage = image.fit(thumbnailWidth, thumbnailHeight)

      var imgDir = new File(homeDir + File.separator + LocalDate.now)
      if (!imgDir.exists()) {
        imgDir.mkdirs()
      }
      val now = LocalTime.now()

      thumbnailImage.output(imgDir.getAbsolutePath + File.separator + "sm" + now + ".png")
      image.output(imgDir.getAbsolutePath + File.separator + now + ".png")

      // clean up old data
    }
    case _ => println("received unknown message")
  }
}
