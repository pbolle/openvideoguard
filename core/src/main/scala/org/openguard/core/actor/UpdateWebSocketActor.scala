package org.openguard.core.actor

import akka.actor.{Actor, ActorRef, Props}

import scala.collection.mutable.ListBuffer

/**
 * Created by pbolle on 25.12.15.
 */
object UpdateWebSocketActor {
  var actorList = new ListBuffer[ActorRef]()

  def register(out: ActorRef) = {
    val updateWebSocketActor = Props(new UpdateWebSocketActor(out))
    actorList += out
    updateWebSocketActor
  }

  def notifyActors(message: String): Unit ={
    actorList = actorList.filter(!_.isTerminated)
    for(actor<-actorList){
      actor ! message
    }
  }
}

class UpdateWebSocketActor (out: ActorRef) extends Actor {
  def receive: Receive =  {
    case msg: String => out !  msg
  }
}
