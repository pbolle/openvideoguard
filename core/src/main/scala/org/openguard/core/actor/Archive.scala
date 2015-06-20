package org.openguard.core.actor

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.sksamuel.scrimage.Image
import org.openguard.core.Photo

/**
 * Created by pbolle on 20.06.15.
 */
class Archive extends Actor {
  def receive: Receive = {
    case photo: Photo => {
      println("@TODO move image to archiv and delete")
    }
  }
}
