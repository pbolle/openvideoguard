import org.openguard.core.FTPApplication
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    FTPApplication.start()
  }
  override def onStop(app: Application) {
    FTPApplication.stop()
  }

}