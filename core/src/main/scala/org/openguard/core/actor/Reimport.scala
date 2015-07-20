package org.openguard.core.actor

import java.sql.Timestamp
import java.util.Date

import akka.actor.Actor
import java.io._

import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models.ImageRef

/**
 * Created by pbolle on 19.07.15.
 */
class Reimport extends Actor {
  val formatDay = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
  def imageRefDAO = new ImageRefDAO

  override def receive: Receive = {
    case path: String => {
      for (subDir <- (new File(path)).listFiles().toStream.filter(_.isDirectory).iterator){
        importSubDir(subDir)
      }
    }
  }

  def importSubDir(subDir: File) = {
    for(file <- subDir.listFiles().toStream.filter(_.getName.endsWith(".png")).filter(!_.getName.startsWith("sm"))){
      val timeStamp = file.getName.substring(0,file.getName.length - 4)

      val date =  formatDay.parse(subDir.getName+" "+timeStamp)

      // insert in db
      var imageRef = new  ImageRef(
        subDir.getName + File.separator + file.getName,
        subDir.getName + File.separator +"sm"+ file.getName,
        new Timestamp(date.getTime),
        subDir.getName.substring(0,4).toInt,
        subDir.getName.substring(5,7).toInt,
        subDir.getName.substring(8,10).toInt,
        timeStamp.substring(0,2).toInt
      )
      imageRefDAO.insert(imageRef)

    }
  }

}
