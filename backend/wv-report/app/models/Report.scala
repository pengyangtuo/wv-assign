package models

import java.io.File
import java.sql.Timestamp

import dto.ResponseReport

/**
  * Created by yangtuopeng on 2017-03-26.
  */
case class Report(id: Int, location: String, uploadedTime: Timestamp, desc: Option[String])

object Report{
  implicit class ReportOps(report: Report) {
    def toDto: ResponseReport = ResponseReport(report.id, report.location, report.uploadedTime, report.desc)
  }
}