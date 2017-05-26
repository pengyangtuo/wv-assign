package controllers

import javax.inject.Inject

import actors.{NotificationMessage, ReportSummaryNotifier, ReportSummaryUpdated}
import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.libs.streams._
import play.api.mvc.WebSocket.MessageFlowTransformer

/**
  * Created by ypeng on 2017-03-27.
  */
class WebsocketController @Inject()(implicit system: ActorSystem, materializer: Materializer)
  extends Controller {

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, JsValue]

  def notifier = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => ReportSummaryNotifier.props(out))
  }
}
