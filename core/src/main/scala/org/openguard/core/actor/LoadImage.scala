package org.openguard.core.actor

import java.io.{File, FileInputStream}
import java.time.{LocalDate, LocalTime}

import akka.actor.{Actor, Props}
import com.sksamuel.scrimage.Image
import org.openguard.core.Photo
import org.openguard.core.models._
import play.api.Play
import play.api.Play.current
import play.api.libs.iteratee._

import scala.reflect.io.Path

/**
 * Created by pbolle on 19.06.15.
 */
class LoadImage extends Actor with ConverterTrait {

  val thumbnailWidth: Int = Play.configuration.getInt("ovg.img.thumbnail.width").getOrElse(256)
  val thumbnailHeight: Int = Play.configuration.getInt("ovg.img.thumbnail.heigth").getOrElse(144)

  def receive = {
    case imagePath: String => {
      implicit val mediatype : String = IMAGE
      implicit val localDate = LocalDate.now
      implicit val now = LocalTime.now

      val stream = new FileInputStream(imagePath)
      val image = Image.fromStream(stream)
      stream.close()

      // crate image object with cam + time + name
      val photo = new Photo(image, imagePath)

      // create thumbnail
      val thumbnailImage = image.fit(thumbnailWidth, thumbnailHeight)

      val imgDir = createTargedDir(localDate)
      val timeStamp = hourFormatHHMMssSSS.format(now);

      // write to file
      thumbnailImage.output(imgDir.getAbsolutePath + File.separator + "sm" + timeStamp + ".png")
      image.output(imgDir.getAbsolutePath + File.separator + timeStamp + ".png")

      // insert in db
      insertIntoDb( timeStamp + ".png","sm" + timeStamp + ".png")

      // clean up old data
      Path(photo.path).delete()

      UpdateWebSocketActor.notifyActors(localDate + File.separator + timeStamp + ".png")
/*
      var updateWebSocketActor = context.actorOf(Props[UpdateWebSocketActor])
      updateWebSocketActor ! localDate + File.separator + timeStamp + ".png"
*/

/*
      val (out, channel) = Concurrent.broadcast[String]
      channel.push(timeStamp + ".png")
*/

    }
    case _ => println("received unknown message")
  }
}
