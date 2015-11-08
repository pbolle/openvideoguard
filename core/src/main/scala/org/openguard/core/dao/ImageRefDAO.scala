package org.openguard.core.dao

import java.sql.Timestamp

import org.openguard.core.models._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.Future

class ImageRefDAO extends HasDatabaseConfig[JdbcProfile] {
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  private val ImageRefs = TableQuery[ImageRefTable]

  import driver.api._

  def all(): Future[List[ImageRef]] = db.run(ImageRefs.result).map(_.toList)

  def recent(time:Timestamp,mediatype: String, page: Int, pageSize: Long): Future[List[ImageRef]] = {
    // TODO refacture me
    def createFilter(mediatype: String): slick.lifted.Query[ImageRefTable, ImageRef, Seq] = {
      if (mediatype.equals(ALL_MEDIA)) {
        ImageRefs
      } else {
        ImageRefs.filter(_.mediatype === mediatype)
      }
    }
    db.run(createFilter(mediatype).filter(_.uploadTime <= time).sortBy(_.uploadTime.desc).drop((page - 1) * pageSize).take(pageSize).result).map(_.toList)
  }

  def count(mediatype: String): Future[Long] = {
    // TODO refacture me
    def createFilter(mediatype: String): slick.lifted.Query[ImageRefTable, ImageRef, Seq] = {
      if (mediatype.equals(ALL_MEDIA)) {
        ImageRefs
      } else {
        ImageRefs.filter(_.mediatype === mediatype)
      }
    }
    db.run(createFilter(mediatype).length.result).map(_.toLong)
  }

  def insert(imageRef: ImageRef) = {
    db.run(ImageRefs.insertOrUpdate(imageRef))
  }

  def selectDeleteFrames(mediatype: String, startTime: Timestamp, maxEvents: Int) = {
    db.run(ImageRefs
      .filter(_.mediatype === mediatype).filter(_.uploadTime <= startTime)
      .groupBy(ir => (ir.year, ir.month, ir.day, ir.hour))
      .map { case ((year, month, day, hour), group) => ((year, month, day, hour), group.map(_.imgPath).size) }
      .filter { case ((year, month, day, hour), size) => size > maxEvents }
      .map(_._1)
      .result
    ).map(_.toList)
  }

  def selectEventsPerHour(mediatype: String) = {
    db.run(ImageRefs
      .filter(_.mediatype === mediatype)
      .groupBy(ir => (ir.year, ir.month, ir.day, ir.hour))
      .map { case ((year, month, day, hour), group) => (year, month, day, hour, group.map(_.imgPath).size) }
      .result
    ).map(_.toList)
  }

  def findAllInFrame(frame: (Int, Int, Int, Int)): Future[List[ImageRef]] = {
    db.run(ImageRefs
      .filter(_.year === frame._1)
      .filter(_.month === frame._2)
      .filter(_.day === frame._3)
      .filter(_.hour === frame._4)
      .result
    ).map(_.toList)
  }

  def delete(id: String) {
    db.run(ImageRefs.filter(_.imgPath === id).delete)
  }


  /**
   * Table Definition
   */
  private class ImageRefTable(tag: Tag) extends Table[ImageRef](tag, "IMAGEREF") {


    def imgPath = column[String]("IMGPATH", O.PrimaryKey)

    def thumbnailPath = column[String]("TNPATH")

    def uploadTime = column[Timestamp]("UPLOADTIME")

    def year = column[Int]("YEAR")

    def month = column[Int]("MONTH")

    def day = column[Int]("DAY")

    def hour = column[Int]("HOUR")

    def mediatype = column[String]("MEDIATYPE")

    def * = (imgPath, thumbnailPath, uploadTime, year, month, day, hour, mediatype) <>(ImageRef.tupled, ImageRef.unapply _)
  }

}