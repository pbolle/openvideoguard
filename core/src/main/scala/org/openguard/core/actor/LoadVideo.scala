package org.openguard.core.actor

import java.io.File
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}
import java.util.Date

import akka.actor.Actor
import org.apache.commons.io.FileUtils
import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models._
import play.api.Play
import play.api.Play.current

import scala.reflect.io.Path
import scala.sys.process._


/**
 * Created by pbolle on 02.08.15.
 */
class LoadVideo extends Actor {
  def imageRefDAO = new ImageRefDAO

  var homeDir = Play.configuration.getString("ovg.publicDirectory").getOrElse("~/")
  var tempDir = Play.configuration.getString("ovg.tempDir").getOrElse("~/temp")
  val formatDay = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  def receive = {
    case videoPath: String => {
      val tempImtPath = tempDir + File.separator + "loadVideo_" + System.currentTimeMillis()
      var tempImgDir = new File(tempImtPath)
      if (!tempImgDir.exists) {
        tempImgDir.mkdirs
      }
      // convert movie to pictures
      val toImagesCommand = "avconv -i " + videoPath + " -r 1 -ss 00:00:05 -t 00:01:03 -f image2 " + tempImgDir + File.separator + "lva%04d.png"
      // TODO check exitcode
      val toImagesExitcode = toImagesCommand !

      // create animated gif
      val animatedGifName = "lva" + System.currentTimeMillis() + ".gif"
      val toAnimatedGifCommand = "convert -delay 2 -loop 0 " + tempImgDir + File.separator + "*.png -scale 256x144 " + tempImgDir + File.separator + animatedGifName
      // TODO check exitcode
      val toAnimatedGifExitcode = toAnimatedGifCommand !

      // TODO remove duplicate code in LoadImage
      val localDate = LocalDate.now
      var imgDir = new File(homeDir + File.separator + localDate)
      if (!imgDir.exists) {
        imgDir.mkdirs
      }
      val now = LocalTime.now
      val timeStamp = formatDay.format(now);

      // copy to targed
      val videoType = videoPath.substring(videoPath.lastIndexOf("."))
      val targedGif = imgDir.getAbsolutePath + File.separator + "sm" + timeStamp + ".gif"
      val targetVideo = imgDir.getAbsolutePath + File.separator + timeStamp + videoType

      FileUtils.copyFile(new File(tempImgDir + File.separator + animatedGifName), new File(targedGif))
      FileUtils.copyFile(new File(videoPath), new File(targetVideo))

      // insert in db
      var imageRef = new ImageRef(
        localDate + File.separator + timeStamp + videoType,
        localDate + File.separator + "sm" + timeStamp + ".gif",
        new Timestamp((new Date()).getTime),
        localDate.getYear,
        localDate.getMonthValue,
        localDate.getDayOfMonth,
        now.getHour,
        VIDEO
      )
      imageRefDAO.insert(imageRef)

      // delete tempdir
      Path(tempImgDir).deleteRecursively()
      Path(videoPath).delete()
    }
    case _ => println("received unknown message")
  }
}
