package controllers

import javax.inject.Inject

import play.api.{Configuration, Logger}
import play.api.mvc.{Action, Controller}
import services.reportSummary.ReportSummaryService

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ypeng on 2017-03-27.
  */
class ReportSummaryController @Inject()(reportSummaryService: ReportSummaryService, config: Configuration) extends Controller {

  val log = Logger(this.getClass)

  def getAll = Action.async { _ =>
    getReportRecords
  }

  def getReportRecords = {
    reportSummaryService
      .list
      .map{
        case Right(summary) =>
          Ok(summary.map(_.toDto).toJson)
        case Left(_) =>
          ServiceUnavailable
      }
  }

}
