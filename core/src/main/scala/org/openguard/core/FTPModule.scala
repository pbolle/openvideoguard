package org.openguard.core

import java.util.{HashMap, Map}
import javax.inject.{Inject, Provider, Singleton}

import akka.actor.ActorSystem
import org.apache.ftpserver.ftplet.{Ftplet, UserManager}
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.{ClearTextPasswordEncryptor, PropertiesUserManagerFactory}
import org.apache.ftpserver.{FtpServer, FtpServerFactory}
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

object FTPApplication {
  var dbApi: DBApi = null
  var started = false

  def start(): Unit = {
    println("dbApi" + dbApi.database("default"))

    // init AKKA
    implicit val actorsystem = ActorSystem("openvideoguard")

    println("Start FTP Server")
    // init FTP
    val serverFactory: FtpServerFactory = new FtpServerFactory()
    val listenerFactory: ListenerFactory = new ListenerFactory()

    listenerFactory.setPort(2221);
    listenerFactory.setServerAddress("localhost")
    listenerFactory.setImplicitSsl(false);

    serverFactory.addListener("default", listenerFactory.createListener())

    val userManagerFactory: PropertiesUserManagerFactory = new PropertiesUserManagerFactory()

    userManagerFactory.setUrl(getClass.getResource("/userManager.properties"))
    userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor())
    val userManager: UserManager = userManagerFactory.createUserManager()

    // init ftplet
    val ftplets: Map[String, Ftplet] = new HashMap[String, Ftplet]
    ftplets.put("imageFtplet", new ImageFtplet)
    serverFactory.setFtplets(ftplets);

    serverFactory.setUserManager(userManager)

    // start the server
    val server: FtpServer = serverFactory.createServer()
    server.start()

  }
}

class FTPApplication {
}

@Singleton
class FTPApplicationProvider @Inject()(dbApi: DBApi) extends Provider[FTPApplication] {
  var instannce = None: Option[FTPApplication]
  val get = {
    if (FTPApplication.dbApi == null) {
      FTPApplication.dbApi = dbApi
    }
    new FTPApplication
  }
}
