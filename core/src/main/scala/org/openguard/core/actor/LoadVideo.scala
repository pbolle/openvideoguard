package org.openguard.core.actor

import java.io.File

import akka.actor.Actor
import play.api.Play
import play.api.Play.current

import scala.sys.process._


/**
 * Created by pbolle on 02.08.15.
 */
class LoadVideo extends Actor {
  var homeDir = Play.configuration.getString("ftp.homedirectory").getOrElse("~/")
  var tempDir = Play.configuration.getString("ovg.tempDir").getOrElse("~/temp")

  def receive = {
    case videoPath: String => {
      var tempImgDir = new File(tempDir + File.separator + "loadVideo_" + System.currentTimeMillis())
      if (!tempImgDir.exists) {
        tempImgDir.mkdirs
      }
      val toImagesCommand = "avconv -i " + videoPath + " -r 1 -ss 00:00:05 -t 00:01:03 -f image2 " + tempImgDir + File.separator + "lva%04d.png"
      println(toImagesCommand !)
      val animatedGifName = "lva" + System.currentTimeMillis() + ".gif"
      val toAnimatedGifCommand = "convert -delay 2 -loop 0 " + tempImgDir + File.separator + "*.png -scale 256x144 " + tempImgDir + File.separator + animatedGifName
      println(toAnimatedGifCommand !)

    }
    case _ => println("received unknown message")
  }
}
