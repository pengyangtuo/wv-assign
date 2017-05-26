package models

import java.sql.Timestamp
import java.util.UUID

import dto.ResponseReportRecord

/**
  * Created by yangtuopeng on 2017-03-26.
  */
case class ReportRecord(
                         id: Int,
                         reportId: Int,
                         employeeId: Int,
                         hours: Double,
                         rate: Double,
                         date: Timestamp
                       )

object ReportRecord {
  implicit class ReportRecordOps(reportRecord: ReportRecord) {
    def toDto = ResponseReportRecord(
      reportRecord.id,
      reportRecord.reportId,
      reportRecord.employeeId,
      reportRecord.hours,
      reportRecord.rate,
      reportRecord.date
    )
  }
}
