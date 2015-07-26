package org.openguard.core.actor

import java.io.File
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}
import java.util.Date

import akka.actor.{Actor, Props}
import com.sksamuel.scrimage.Image
import org.openguard.core.Photo
import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models.ImageRef
import play.api.Play
import play.api.Play.current

/**
 * Created by pbolle on 19.06.15.
 */
class LoadImage extends Actor {
  def imageRefDAO = new ImageRefDAO
  val thumbnailWidth: Int = Play.configuration.getInt("ovg.img.thumbnail.width").getOrElse(256)
  val thumbnailHeight: Int = Play.configuration.getInt("ovg.img.thumbnail.heigth").getOrElse(144)
  var homeDir = Play.configuration.getString("ftp.homedirectory").getOrElse("~/")
  val formatDay = DateTimeFormatter.ofPattern("hh:mm:ss.SSS")

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

      val localDate = LocalDate.now
      var imgDir = new File(homeDir + File.separator + localDate)
      if (!imgDir.exists) {
        imgDir.mkdirs
      }
      val now = LocalTime.now
      val timeStamp = formatDay.format(now);

      // write to file
      thumbnailImage.output(imgDir.getAbsolutePath + File.separator + "sm" + timeStamp + ".png")
      image.output(imgDir.getAbsolutePath + File.separator + timeStamp + ".png")

      // insert in db
      var imageRef = new  ImageRef(
        localDate + File.separator + timeStamp + ".png",
        localDate + File.separator +"sm"+ timeStamp + ".png",
        new Timestamp((new Date()).getTime),
        localDate.getYear,
        localDate.getMonthValue,
        localDate.getDayOfMonth,
        now.getHour
      )
      imageRefDAO.insert(imageRef)
      // clean up old data
    }
    case _ => println("received unknown message")
  }
}
