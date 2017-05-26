package dto

import java.sql.Timestamp

import play.api.libs.json.{Json, Writes}

/**
  * Created by yangtuopeng on 2017-03-27.
  */
case class ResponseReportRecord(
                                 id: Int,
                                 reportId: Int,
                                 employeeId: Int,
                                 hours: Double,
                                 rate: Double,
                                 date: Timestamp
                               )

object ResponseReportRecord {
  implicit val writeResponseReport: Writes[ResponseReportRecord] = Json.writes[ResponseReportRecord]

  implicit class ResponseReportRecordOps(responseRecordReport: ResponseReportRecord) {
    def toJson = Json.toJson(responseRecordReport)(writeResponseReport)
  }

  implicit class writeResponseReportRecordListOps(responseReportRecordList: List[ResponseReportRecord]) {
    def toJson = Json.toJson(responseReportRecordList)
  }
}
