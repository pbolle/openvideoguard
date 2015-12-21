package org.openguard.core.actor

import java.io.File
import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import org.openguard.core.dao.ImageRefDAO
import org.openguard.core.models._
import play.api.Play
import play.api.Play.current
import java.time.{LocalDate, LocalTime}

/**
 * Created by pbolle on 21.12.15.
 */
trait ConverterTrait {
  def imageRefDAO = new ImageRefDAO
  var tempDir = Play.configuration.getString("ovg.tempDir").getOrElse("~/temp")
  var homeDir = Play.configuration.getString("ovg.publicDirectory").getOrElse("~/")
  val hourFormatHHMMssSSS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  def createTempDirectory(): File = {
    val tempImtPath = tempDir + File.separator + "loadVideo_" + System.currentTimeMillis()
    var tempImgDir = new File(tempImtPath)
    if (!tempImgDir.exists) {
      tempImgDir.mkdirs
    }
    tempImgDir
  }
  def createTargedDir(localDate: LocalDate): File = {
    var imgDir = new File(homeDir + File.separator + localDate)
    if (!imgDir.exists) {
      imgDir.mkdirs
    }
    imgDir
  }

  def insertIntoDb(baseFile: String,thumbnailFile: String)(implicit mediatype:String, now: LocalTime,localDate: LocalDate): Unit ={
      // insert in db
      var imageRef = new ImageRef(
        localDate + File.separator + baseFile,
        localDate + File.separator + thumbnailFile,
        new Timestamp((new Date()).getTime),
        localDate.getYear,
        localDate.getMonthValue,
        localDate.getDayOfMonth,
        now.getHour,
        mediatype
      )
      imageRefDAO.insert(imageRef)
  }
}
