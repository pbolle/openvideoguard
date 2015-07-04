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
  private val PAGE_SIZE = 18

  import driver.api._


  def all(): Future[List[ImageRef]] = db.run(ImageRefs.result).map(_.toList)

  def recent(page: Int): Future[List[ImageRef]] = {
    db.run(ImageRefs.sortBy(_.uploadTime.desc).drop(page * PAGE_SIZE).take(PAGE_SIZE).result).map(_.toList)
  }

  def count(): Future[Long] = {
    db.run(ImageRefs.length.result).map(_.toLong)
  }

  /**
   * Table Definition
   */
  private class ImageRefTable(tag: Tag) extends Table[ImageRef](tag, "IMAGEREF") {


    def path = column[String]("PATH", O.PrimaryKey)

    def uploadTime = column[Timestamp]("UPLOADTIME")

    def year = column[Int]("YEAR")

    def month = column[Int]("MONTH")

    def day = column[Int]("DAY")

    def hour = column[Int]("HOUR")

    def * = (path, uploadTime, year, month, day, hour) <>(ImageRef.tupled, ImageRef.unapply _)
  }

}