package actors

import java.sql.Timestamp

import models.ReportRecord
import play.api.libs.json._

/**
  * Created by ypeng on 2017-03-27.
  */

/**
  * ServiceMessage defines messages passed between actors indicating service event
  */
trait ServiceMessage
case class ReportRecordGenerated(records: List[ReportRecord]) extends ServiceMessage

/**
  * NotificationMessage defines messages send to Notifier actors, NotificationMessage will eventually
  * be converted into json and send back to client
  */
trait NotificationMessage
case class ReportSummaryUpdated(lastUpdateTime: Timestamp, service: String = "summary") extends NotificationMessage

object ReportSummaryUpdated {

  implicit val writeReportSummaryUpdated: Writes[ReportSummaryUpdated] = Json.writes[ReportSummaryUpdated]

  implicit class ResponseReportOps(reportSummaryUpdated: ReportSummaryUpdated) {
    def toJson = Json.toJson(reportSummaryUpdated)(writeReportSummaryUpdated)
  }
}