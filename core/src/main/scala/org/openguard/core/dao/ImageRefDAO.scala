package org.openguard.core.dao

import java.sql.Timestamp

import org.openguard.core.models.ImageRef
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

  def recent(page: Int, pageSize: Long): Future[List[ImageRef]] = {
    db.run(ImageRefs.sortBy(_.uploadTime.desc).drop((page - 1) * pageSize).take(pageSize).result).map(_.toList)
  }

  def count(): Future[Long] = {
    db.run(ImageRefs.length.result).map(_.toLong)
  }

  def insert(imageRef: ImageRef) = {
    db.run(ImageRefs.insertOrUpdate(imageRef))
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