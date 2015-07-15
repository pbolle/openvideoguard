import javax.inject.{Inject, Provider, Singleton}

import play.api.db.DBApi
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

/**
 * Created by pbolle on 12.07.15.
 */
class FTPModule() extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FTPApplication].toProvider[FTPApplicationProvider].eagerly
    )
  }
}

@Singleton
class FTPApplication @Inject()(dbApi: DBApi) {
  def start(): Unit = {
    println("test")
  }

  start()
}

@Singleton
class FTPApplicationProvider @Inject()(dbApi: DBApi) extends Provider[FTPApplication] {

  lazy val get = new FTPApplication(dbApi)
}
