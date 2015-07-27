package org.openguard.core

import akka.actor.{ActorSystem, Props}
import org.apache.ftpserver.ftplet.{DefaultFtplet, FtpRequest, FtpSession, FtpletResult}
import org.openguard.core.actor.LoadImage

/**
 * Created by pbolle on 14.06.15.
 */
class ImageFtplet(implicit actorsystem: ActorSystem) extends DefaultFtplet {
  override def onUploadEnd(session: FtpSession, request: FtpRequest): FtpletResult = {
    val loadImage = actorsystem.actorOf(Props[LoadImage])
    loadImage ! session.getUser.getHomeDirectory + "/" + request.getArgument

    return FtpletResult.DEFAULT
  }
}