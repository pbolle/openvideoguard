package controllers

import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import org.openguard.core.actor.Reimport
import org.openguard.core.models._
import play.api.Play
import play.api.Play.current
import play.api.mvc.{Action, Controller}

class ReindexController @Inject()(system: ActorSystem) extends Controller {

  def index() = Action {
    var reindexActor = system.actorOf(Props[Reimport])
    reindexActor ! Play.configuration.getString("ovg.publicDirectory").getOrElse("~/")
    Redirect(controllers.routes.EventController.index(ALL_MEDIA, 1))
  }

}
