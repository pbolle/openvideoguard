package org.openguard.core

import akka.actor.{ActorSystem, Props}
import org.apache.ftpserver.ftplet.{DefaultFtplet, FtpRequest, FtpSession, FtpletResult}
import org.openguard.core.actor.LoadImage
import play.api.Play
import play.api.Play.current

/**
 * Created by pbolle on 14.06.15.
 */
class ImageFtplet(implicit actorsystem: ActorSystem) extends DefaultFtplet {
  override def onUploadEnd(session: FtpSession, request: FtpRequest): FtpletResult = {

    val imageExtensions = Play.configuration.getStringSeq("ovg.extensions.img").getOrElse(Seq(".jpg",".png",".gif"))
    val videoExtensions = Play.configuration.getStringSeq("ovg.extensions.video").getOrElse(Seq(".avi",".mpg",".mpeg",".mp4"))

    val extension = request.getArgument.substring(request.getArgument.lastIndexOf("."))

    // TODO use match
/*
    extension match {
      case imageExtensions => { println("image")}
      case videoExtensions => { println("video")}
    }
*/
    if (imageExtensions.contains(extension)){
      val loadImage = actorsystem.actorOf(Props[LoadImage])
      loadImage ! session.getUser.getHomeDirectory + "/" + request.getArgument
    } else if (videoExtensions.contains(extension)) {
      println("video")
    }


    return FtpletResult.DEFAULT
  }
}