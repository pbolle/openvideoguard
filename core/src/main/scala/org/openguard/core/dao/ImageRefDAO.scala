package org.openguard.core.dao

import java.sql.Timestamp
import java.util.Date

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
  private val PAGE_SIZE = 20

  import driver.api._

  def all(): Future[List[ImageRef]] = db.run(ImageRefs.result).map(_.toList)

  def recent(): Future[List[ImageRef]] = db.run(ImageRefs.take(PAGE_SIZE).result).map(_.toList)

  /**
   * Table Definition
   */
  private class ImageRefTable(tag: Tag) extends Table[ImageRef](tag, "IMAGEREF") {
    implicit val dateColumnType =
      MappedColumnType.base[Date, Timestamp](
        d => new Timestamp(d.getTime),
        d => new Date(d.getTime))

    def path = column[String]("PATH", O.PrimaryKey)

    def uploadTime = column[Date]("UPLOADTIME")

    def year = column[Int]("YEAR")

    def month = column[Int]("MONTH")

    def day = column[Int]("DAY")

    def hour = column[Int]("HOUR")

    def * = (path, uploadTime, year, month, day, hour) <>(ImageRef.tupled, ImageRef.unapply _)
  }

}