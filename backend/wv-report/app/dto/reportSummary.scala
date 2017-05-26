package dto

import java.sql.Timestamp

import play.api.libs.json.{Json, Writes}

/**
  * Created by ypeng on 2017-03-27.
  */
case class ResponseReportSummary(
                          employeeId: Int,
                          payPeriodStart: Timestamp,
                          payPeriodEnd: Timestamp,
                          amount: Double,
                          createdTime: Timestamp,
                          lastModifiedTime: Timestamp
                        )

object ResponseReportSummary {
  implicit val writeResponseReportSummary: Writes[ResponseReportSummary] = Json.writes[ResponseReportSummary]

  implicit class ResponseReportSummaryOps(responseSummaryReport: ResponseReportSummary) {
    def toJson = Json.toJson(responseSummaryReport)(writeResponseReportSummary)
  }

  implicit class writeResponseReportSummaryListOps(responseReportSummaryList: List[ResponseReportSummary]) {
    def toJson = Json.toJson(responseReportSummaryList)
  }
}

