package actors

import java.sql.Timestamp
import java.util.Calendar

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import models.ReportRecord
import services.reportRecord.ReportRecordService
import services.reportSummary.ReportSummaryService
import wv.service.services.ServiceError

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Administrator Actor listening on the ServiceMessage channel, orchestrate messages sent by
  * every services and generate corresponding command for them
  */
class ReportServiceAdmin(reportRecordService: ReportRecordService, reportSummaryService: ReportSummaryService) extends Actor with ActorLogging{

  override def preStart(): Unit = {
    log.info("created actor: " + self.path)
    context.system.eventStream.subscribe(context.self, classOf[ServiceMessage])
  }

  override def postStop() = {
    context.system.eventStream.unsubscribe(context.self)
  }

  override def receive = {
    case ReportRecordGenerated(records) =>
      createReportRecords(records)
        .map{
          case Left(error) => log.error("unable to create report records in database: " + error.toString)
          case Right(_) =>
            val summaries = reportSummaryService.generateSummaryFromRecords(records)
            reportSummaryService.upsertBatchSummary(summaries)
              .map{
                case Left(error) => log.error("unable to create report summary in database: " + error.toString)
                case Right(_) =>
                  log.info("finished create record and summary, sending notification")
                  val now = new Timestamp(Calendar.getInstance.getTime.getTime)
                  context.system.eventStream.publish(ReportSummaryUpdated(now))
              }
        }

    case _ => None  // ignore other messages
  }

  def createReportRecords(records: List[ReportRecord]): Future[Either[ServiceError, Int]] =
    reportRecordService
      .createBatch(records)
}

object ReportServiceAdmin {
  def props(reportRecordService: ReportRecordService, reportSummaryService: ReportSummaryService) = Props(new ReportServiceAdmin(reportRecordService, reportSummaryService))
}
