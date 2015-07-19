package org.openguard.core.actor

import akka.actor.Actor
import org.openguard.core.Photo

import scala.reflect.io.File

/**
 * Created by pbolle on 20.06.15.
 */
class Archive extends Actor {
  def receive: Receive = {
    case photo: Photo => {
      var file = File(photo.path)
      if(file.exists){
        file.delete()
      }
    }
  }
}
