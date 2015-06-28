package controllers

import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.mvc.{Action, Controller}
import slick.driver.JdbcProfile
import tables.ImageRefTable
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application extends Controller with ImageRefTable with HasDatabaseConfig[JdbcProfile] {
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import driver.api._
  //create an instance of the table
  val Cats = TableQuery[ImageRefs] //see a way to architect your app in the computers-database sample

  def index = Action.async {
    db.run(Cats.result).map(res => Ok(views.html.index(res.toList)))
  }


}
