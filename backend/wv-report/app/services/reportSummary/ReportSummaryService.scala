package services.reportSummary

import java.sql.Timestamp

import models.{ReportRecord, ReportSummary}
import wv.service.services.{ApiService, ServiceError}

import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-27.
  */
trait ReportSummaryService extends ApiService[ReportSummary, (Int, Timestamp, Timestamp)]{
  /**
    * Generate a list of summary entries base on the input report records
    * @param records
    * @return
    */
  def generateSummaryFromRecords(records: List[ReportRecord]): List[ReportSummary]

  /**
    * Insert or merge the input summary entry list to database
    * @param summary
    * @return
    */
  def upsertBatchSummary(summary: List[ReportSummary]): Future[Either[ServiceError, Int]]
}
