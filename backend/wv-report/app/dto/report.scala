package dto

import java.io.File
import java.sql.Timestamp
import java.util.Calendar

import models.Report
import play.api.libs.json.{Json, Writes}

import scala.io.Source

/**
  * Created by ypeng on 2017-03-24.
  */
case class CreateReport(file: File)
case class ResponseReport(id: Int, location: String, uploadedTime: Timestamp, desc: Option[String])

object CreateReport {
  implicit class CreateReportOps(createReport: CreateReport) {
    def toModel: Report = {
      val bufferedSource = Source.fromFile(createReport.file.getPath)
      val recordIterator = bufferedSource.getLines
      /* There is a trade-off here, need to scan the file once to get the report id,
         but if find duplicate id, can save the call to get all PayGroup info
       */
      val reportId = recordIterator.toList match {
            case _ :+ footer => footer.split(",").map(_.trim).apply(1)
          }
      val now = new Timestamp(Calendar.getInstance.getTime.getTime)

      Report(reportId.toInt, createReport.file.getAbsolutePath, now, None)
    }
  }
}

object ResponseReport {
  implicit val writeResponseReport: Writes[ResponseReport] = Json.writes[ResponseReport]

  implicit class ResponseReportOps(responseReport: ResponseReport) {
    def toJson = Json.toJson(responseReport)(writeResponseReport)
  }

  implicit class writeResponseReportListOps(responseReportList: List[ResponseReport]) {
    def toJson = Json.toJson(responseReportList)
  }
}