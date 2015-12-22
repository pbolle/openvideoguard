package org.openguard.core

import java.util.{HashMap, Map}
import javax.inject.{Inject, Provider, Singleton}

import akka.actor.{ActorSystem, Props}
import org.apache.ftpserver.ftplet.Ftplet
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor
import org.apache.ftpserver.{FtpServer, FtpServerFactory}
import org.openguard.core.actor.DeleteImage
import org.openguard.core.ftp.UserManager
import play.api.Play.current
import play.api.db.DBApi
import play.api.inject.{Binding, Module}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.{Configuration, Environment, Play}

import scala.concurrent.duration._

/**
 * Module to initialise the FTP Server
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
  val DELETE_CRON_CONFIG_KEY = ""

  def start(): Unit = {
    // init AKKA
    implicit val actorsystem = ActorSystem("openvideoguard")

    // init FTP
    val serverFactory: FtpServerFactory = new FtpServerFactory()
    val listenerFactory: ListenerFactory = new ListenerFactory()

    listenerFactory.setPort(Play.configuration.getInt("ovg.ftpserver.port").getOrElse(2221));
    listenerFactory.setServerAddress(Play.configuration.getString("ovg.ftpserver.serveraddress").getOrElse("0.0.0.0"))
    listenerFactory.setImplicitSsl(false);

    serverFactory.addListener("default", listenerFactory.createListener())

    val selectAllStmt = "SELECT userid FROM FTP_USER ORDER BY userid"
    val selectUserStmt = "SELECT userid, userpassword, homedirectory, enableflag, writepermission, idletime, uploadrate, downloadrate, maxloginnumber, maxloginperip FROM FTP_USER WHERE userid = '{userid}'"
    val insertUserStmt = "INSERT INTO FTP_USER (userid, userpassword, homedirectory, enableflag, writepermission, idletime, uploadrate,  downloadrate) VALUES ('{userid}', '{userpassword}', '{homedirectory}',  {enableflag}, {writepermission}, {idletime}, {uploadrate}, {downloadrate})"
    val updateUserStmt = "UPDATE FTP_USER SET userpassword='{userpassword}',homedirectory='{homedirectory}',enableflag={enableflag},writepermission={writepermission},idletime={idletime},uploadrate={uploadrate},downloadrate={downloadrate} WHERE userid='{userid}"
    val deleteUserStmt = "DELETE FROM FTP_USER WHERE userid = '{userid}'"
    val authenticateStmt = "SELECT userpassword from FTP_USER WHERE userid='{userid}'"
    val isAdminStmt = "SELECT userid FROM FTP_USER WHERE userid='{userid}' AND userid='admin'"
    var adminName = "admin"

    val userManager = new UserManager(dbApi.database("default").dataSource, selectAllStmt, selectUserStmt, insertUserStmt, updateUserStmt, deleteUserStmt, authenticateStmt, isAdminStmt, new ClearTextPasswordEncryptor(), adminName)

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

    /*
        @TODO could this be easyer
        for(deleterule <- Play.configuration.getList("ovg.crone.deleterules")){
          println (deleterule.unwrapped().get(0).asInstanceOf[HashMap].get("dropRate"))
        }
    */
    var i = 1
    val playConf = Play.configuration
    while (playConf.getInt("ovg.crone.deleterule." + i + ".dropRate").isDefined
      && playConf.getInt("ovg.crone.deleterule." + i + ".maxEvents").isDefined
      && playConf.getInt("ovg.crone.deleterule." + i + ".delteAfterDays").isDefined) {
      val dropRate = playConf.getInt("ovg.crone.deleterule." + i + ".dropRate").get
      val maxEvents = playConf.getInt("ovg.crone.deleterule." + i + ".maxEvents").get
      val delteAfterDays = playConf.getInt("ovg.crone.deleterule." + i + ".delteAfterDays").get
      val mediaType = playConf.getString("ovg.crone.deleterule." + i + ".mediaType").get

      actorsystem.scheduler.schedule(10 seconds, 1 hours, deleteImageActor, new DeleteRule(dropRate,maxEvents,delteAfterDays,mediaType))

      i = i + 1
    }

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
