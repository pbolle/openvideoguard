package org.openguard.core.actor

import java.io.File
import java.time.{LocalDate, LocalTime}

import akka.actor.Actor
import org.apache.commons.io.FileUtils
import org.openguard.core.models._

import scala.reflect.io.Path
import scala.sys.process._


/**
 * Created by pbolle on 02.08.15.
 */
class LoadVideo extends Actor with ConverterTrait {

  def receive = {
    case videoPath: String => {
      implicit val mediatype : String = VIDEO
      implicit val localDate = LocalDate.now
      implicit val now = LocalTime.now
      val timeStamp = hourFormatHHMMssSSS.format(now);

      val tempImgDir = createTempDirectory()

      // convert movie to pictures
      val toImagesCommand = "avconv -i " + videoPath + " -r 1 -ss 00:00:05 -t 00:01:03 -f image2 " + tempImgDir + File.separator + "lva%04d.png"
      // TODO check exitcode
      val toImagesExitcode = toImagesCommand !

      // convert movie to target Formtat
      val webmTempFile = tempImgDir + File.separator + timeStamp + ".webm"
      val toWebmCommand = "avconv -i " + videoPath + " " + webmTempFile
      val toWebmExitcode = toWebmCommand !

      // create animated gif from pictures
      val animatedGifName = "lva" + System.currentTimeMillis() + ".gif"
      val toAnimatedGifCommand = "convert -delay 2 -loop 0 " + tempImgDir + File.separator + "*.png -scale 256x144 " + tempImgDir + File.separator + animatedGifName
      // TODO check exitcode
      val toAnimatedGifExitcode = toAnimatedGifCommand !

      // copy to targed
      val imgDir = createTargedDir(localDate)
      val targedGif = imgDir.getAbsolutePath + File.separator + "sm" + timeStamp + ".gif"
      val targetVideo = imgDir.getAbsolutePath + File.separator + timeStamp + ".webm"

      FileUtils.copyFile(new File(tempImgDir + File.separator + animatedGifName), new File(targedGif))
      FileUtils.copyFile(new File(webmTempFile), new File(targetVideo))

      // insert in db
      insertIntoDb(timeStamp +  ".webm" ,"sm" + timeStamp + ".gif")

      // delete tempdir
      Path(tempImgDir).deleteRecursively()
      Path(videoPath).delete()
    }
    case _ => println("received unknown message")
  }

}
