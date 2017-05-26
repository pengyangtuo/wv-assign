package actors

import java.security.Timestamp

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import models.ReportRecord

/**
  * Websocket Actor listening on the NotificationMessage channel, forward notifications to
  * any client with websocket connection
  */
class ReportSummaryNotifier(out: ActorRef) extends Actor with ActorLogging{

  override def preStart(): Unit = {
    log.info(s"new ws connection established by actor[${context.self.path}]")
    context.system.eventStream.subscribe(context.self, classOf[NotificationMessage])
  }

  override def postStop() = {
    context.system.eventStream.unsubscribe(context.self)
  }

  override def receive = {
    case msg: ReportSummaryUpdated => out ! msg.toJson
    case _ => None
  }
}

object ReportSummaryNotifier {
  def props(out: ActorRef) = Props(new ReportSummaryNotifier(out))
}
