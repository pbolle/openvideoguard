package org.openguard.core.actor

import java.io.File

import akka.actor.{Props, Actor}
import com.sksamuel.scrimage.Image
import org.openguard.core.Photo

/**
 * Created by pbolle on 19.06.15.
 */
class LoadImage extends Actor {
  def receive = {
    case imagePath: String => {
      val image = Image.apply(new File(imagePath))
      println("image " + image)
      // crate image object with cam + time + name
      val photo = new Photo(image,imagePath)
      // move to archive
      var archiveActor = context.actorOf(Props[Archive])
      archiveActor ! photo
      // create overview

      // clean up old data
    }
    case _ => println("received unknown message")
  }
}
