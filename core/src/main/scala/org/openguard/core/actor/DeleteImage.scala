package org.openguard.core.actor

import akka.actor.Actor
import org.openguard.core.DeleteRule

import scala.reflect.io.File

/**
 * Created by pbolle on 20.06.15.
 */
class DeleteImage extends Actor {
  def receive: Receive = {
    case photo: DeleteRule => {
      println("ping")
    }
  }
}
