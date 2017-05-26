package models

import java.sql.Timestamp

import dto.ResponseReportSummary

/**
  * Created by ypeng on 2017-03-27.
  */
case class ReportSummary(
                          employeeId: Int,
                          payPeriodStart: Timestamp,
                          payPeriodEnd: Timestamp,
                          amount: Double,
                          createdTime: Timestamp,
                          lastModifiedTime: Timestamp)

object ReportSummary{
  implicit class ReportSummaryOps(reportSummary: ReportSummary) {
    def toDto = ResponseReportSummary(
      reportSummary.employeeId,
      reportSummary.payPeriodStart,
      reportSummary.payPeriodEnd,
      reportSummary.amount,
      reportSummary.createdTime,
      reportSummary.lastModifiedTime)
  }
}