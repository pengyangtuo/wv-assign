package controllers

import javax.inject.Inject

import play.api.{Configuration, Logger}
import play.api.mvc.{Action, Controller}
import services.reportRecord.ReportRecordService
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yangtuopeng on 2017-03-27.
  */
class ReportRecordController @Inject()(reportRecordService: ReportRecordService, config: Configuration) extends Controller {

  val log = Logger(this.getClass)

  def getAll = Action.async { _ =>
    getReportRecords
  }

  def get(id: Int) = Action.async { _ =>
    getReportRecord(id)
  }

  def getReportRecord(id: Int) = {
    reportRecordService
      .find(id)
      .map{
        case Right(Some(reportRecord)) =>
          Ok(reportRecord.toDto.toJson)
        case Right(None) =>
          NotFound
        case Left(_) =>
          ServiceUnavailable
      }
  }

  def getReportRecords = {
    reportRecordService
      .list
      .map{
        case Right(reports) =>
          Ok(reports.map(_.toDto).toJson)
        case Left(_) =>
          ServiceUnavailable
      }
  }

}
