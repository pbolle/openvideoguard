package controllers

import java.io.{File, FileInputStream}

import org.joda.time.DateTimeZone
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.Play
import play.api.Play.current
import play.api.http.LazyHttpErrorHandler
import play.api.libs._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee._
import play.api.mvc.{Action, AnyContent, ResponseHeader, Result}


/**
 * Created by pbolle on 11.07.15.
 */
class Resource extends AssetsBuilder(LazyHttpErrorHandler) {

  private val timeZoneCode = "GMT"

  //Dateformatter is immutable and threadsafe
  private val df: DateTimeFormatter =
    DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss '" + timeZoneCode + "'").withLocale(java.util.Locale.ENGLISH).withZone(DateTimeZone.forID(timeZoneCode))

  private lazy val defaultCharSet = Play.configuration.getString("default.charset").getOrElse("utf-8")

  private def addCharsetIfNeeded(mimeType: String): String =
    if (MimeTypes.isText(mimeType))
      "; charset=" + defaultCharSet
    else ""

  def at(path: String, file: String): Action[AnyContent] = Action { implicit request =>
    var homeDir = Play.configuration.getString("ftp.homedirectory").getOrElse("~/")
    var responseFile = new File(homeDir + file)

    if (!responseFile.canRead && responseFile.isDirectory) {
      NotFound
    }

    lazy val (length, resourceData) = {
      val stream = new FileInputStream(responseFile)
      try {
        (stream.available, Enumerator.fromStream(stream))
      } catch {
        case _: Throwable => (-1, Enumerator[Array[Byte]]())
      }
    }
    if (length == -1) {
      NotFound
    }


    Result(
      ResponseHeader(OK, Map(
        CONTENT_LENGTH -> length.toString,
        CONTENT_TYPE -> MimeTypes.forFileName(file).map(m => m + addCharsetIfNeeded(m)).getOrElse(BINARY),
        DATE -> df.print({
          new java.util.Date
        }.getTime))),
      resourceData)
  }
}
