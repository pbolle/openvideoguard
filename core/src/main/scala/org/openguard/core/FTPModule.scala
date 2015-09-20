package org.openguard.core

import java.util.{HashMap, Map}
import javax.inject.{Inject, Provider, Singleton}

import akka.actor.{Props, ActorSystem}
import org.apache.ftpserver.ftplet.Ftplet
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor
import org.apache.ftpserver.usermanager.impl.DbUserManager
import org.apache.ftpserver.{FtpServer, FtpServerFactory}
import org.openguard.core.actor.{DeleteImage, Archive}
import play.api.db.DBApi
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
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
  var server: FtpServer = null

  def start(): Unit = {
    // init AKKA
    implicit val actorsystem = ActorSystem("openvideoguard")

    println("Start FTP Server")
    // init FTP
    val serverFactory: FtpServerFactory = new FtpServerFactory()
    val listenerFactory: ListenerFactory = new ListenerFactory()

    listenerFactory.setPort(2221);
    listenerFactory.setServerAddress("0.0.0.0")
    listenerFactory.setImplicitSsl(false);

    serverFactory.addListener("default", listenerFactory.createListener())

    //    val userManagerFactory: PropertiesUserManagerFactory = new PropertiesUserManagerFactory()

    //    userManagerFactory.setUrl(getClass.getResource("/userManager.properties"))
    //    userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor())
    //    val userManager: UserManager = userManagerFactory.createUserManager()
    val selectAllStmt = "SELECT userid FROM FTP_USER ORDER BY userid"
    val selectUserStmt = "SELECT userid, userpassword, homedirectory, enableflag, writepermission, idletime, uploadrate, downloadrate, maxloginnumber, maxloginperip FROM FTP_USER WHERE userid = '{userid}'"
    val insertUserStmt = "INSERT INTO FTP_USER (userid, userpassword, homedirectory, enableflag, writepermission, idletime, uploadrate,  downloadrate) VALUES ('{userid}', '{userpassword}', '{homedirectory}',  {enableflag}, {writepermission}, {idletime}, {uploadrate}, {downloadrate})"
    val updateUserStmt = "UPDATE FTP_USER SET userpassword='{userpassword}',homedirectory='{homedirectory}',enableflag={enableflag},writepermission={writepermission},idletime={idletime},uploadrate={uploadrate},downloadrate={downloadrate} WHERE userid='{userid}"
    val deleteUserStmt = "DELETE FROM FTP_USER WHERE userid = '{userid}'"
    val authenticateStmt = "SELECT userpassword from FTP_USER WHERE userid='{userid}'"
    val isAdminStmt = "SELECT userid FROM FTP_USER WHERE userid='{userid}' AND userid='admin'"
    var adminName = "admin"

    val userManager = new DbUserManager(dbApi.database("default").dataSource, selectAllStmt, selectUserStmt, insertUserStmt, updateUserStmt, deleteUserStmt, authenticateStmt, isAdminStmt, new ClearTextPasswordEncryptor(), adminName)

    // init ftplet
    val ftplets: Map[String, Ftplet] = new HashMap[String, Ftplet]
    ftplets.put("imageFtplet", new ImageFtplet)
    serverFactory.setFtplets(ftplets);

    serverFactory.setUserManager(userManager)

    // start the server
    server = serverFactory.createServer()
    server.start()

    // start cronejob
    // move to archive
    var deleteImageActor = actorsystem.actorOf(Props[DeleteImage])
    // minutes
    actorsystem.scheduler.schedule(5 seconds,5 seconds,deleteImageActor,new DeleteRule)
  }

  def stop() {
    server.stop()
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
