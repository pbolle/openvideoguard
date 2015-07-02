package org.openguard.core.tables

import java.sql.Timestamp
import java.util.Date

import org.openguard.core.models.ImageRef

import slick.driver.JdbcProfile

trait ImageRefTable {
  protected val driver: JdbcProfile

  import driver.api._

  class ImageRefs(tag: Tag) extends Table[ImageRef](tag, "IMAGEREF") {
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