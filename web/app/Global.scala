import java.util.{HashMap, Map}

import akka.actor.ActorSystem
import org.apache.ftpserver.ftplet.{Ftplet, UserManager}
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.{ClearTextPasswordEncryptor, PropertiesUserManagerFactory}
import org.apache.ftpserver.{FtpServer, FtpServerFactory}
import org.openguard.core.ImageFtplet
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
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